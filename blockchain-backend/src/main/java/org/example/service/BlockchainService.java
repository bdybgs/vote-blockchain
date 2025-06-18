// src/main/java/org/example/service/BlockchainService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.*;
import org.example.poa.EcdsaSigner;
import org.example.poa.EcdsaVerifier;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final BlockRepository   blockRepo;
    private final VoteRepository    voteRepo;
    private final OptionRepository  optionRepo;
    private final UserRepository    userRepo;
    private final EcdsaSigner       ecdsaSigner;
    private final EcdsaVerifier     verifier;

    /** Список адресов других нод-валидаторов (в формате http://host:port) */
    @Value("${poa.peers}")
    private List<String> peerUrls;

    /* ────────── Подпись/хэш ────────── */
    private String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Создает блок, подписывает его, сохраняет в свою БД и рассылает остальным нодам-валидаторам.
     *
     * @param user     — кто голосует.
     * @param optionId — ID варианта (Option) (кандидата).
     */
    public Block addVoteToBlockchain(User user, long optionId) throws Exception {
         
        if (!chain.isEmpty()) {
        Block last = chain.get(chain.size() - 1);
        String recalculated = sha256(
            last.getPreviousHash()
          + last.getNumber()
          + last.getTimestamp()
          + last.getEvent().getId()
          + last.getVoter().getId());
            if (!recalculated.equals(last.getCurrentHash()))
        throw new IllegalStateException("Blockchain integrity violated: last hash mismatch");
        }

        
        // 1) Найти вариант и событие
        Option opt = optionRepo.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));
        var event = opt.getEvent();

        // 2) Сохранить сущность Vote
        Vote v = new Vote();
        v.setUser(user);
        v.setEvent(event);
        v.setTimestamp(LocalDateTime.now());
        voteRepo.save(v);

        // 3) Увеличить счётчик votes у Option
        opt.setVotes(opt.getVotes() + 1);
        optionRepo.save(opt);

        // 4) Подготовить новый блок
        List<Block> chain = blockRepo.findAll(Sort.by("number"));
        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getCurrentHash();

        Block block = new Block();
        block.setNumber((long) chain.size() + 1);
        block.setPreviousHash(prevHash);
        block.setTimestamp(LocalDateTime.now());
        block.setEvent(event);
        block.setVoter(user);
        block.setTotalVoters(event.getVotes().size());

       
        // Вычислим SHA-256 от строки (prevHash + номер + timestamp + eventId + userId)
        String raw = prevHash
                + block.getNumber()
                + block.getTimestamp().toString()
                + event.getId()
                + user.getId();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(raw.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte by : digest) {
            sb.append(String.format("%02x", by));
        }
        String computedHash = sb.toString();
        block.setCurrentHash(computedHash);

        // 5) Подписать этот currentHash ECDSA
        byte[] signature = ecdsaSigner.sign(computedHash.getBytes());
        block.setSignature(signature);

        // 6) Сохранить блок в БД
        return blockRepo.save(block);
    }

    /**
     * REST-метод, который слушает новые блоки от других нод-валидаторов.
     * Проверяет подпись и добавляет в свою БД, если всё правильно.
     */
    public Block receiveBlockFromPeer(Block remoteBlock) throws Exception {
        // 1) Проверяем подпись:
        String dataToVerify = remoteBlock.getPreviousHash()
                + remoteBlock.getNumber()
                + remoteBlock.getTimestamp().toString()
                + remoteBlock.getEvent().getId()
                + remoteBlock.getVoter().getId();
        boolean ok = verifier.verify(dataToVerify.getBytes(), remoteBlock.getSignature());
        if (!ok) throw new IllegalArgumentException("Invalid signature from peer");

        // 2) Далее, если номер блока правильный (последовательность), сохраняем:
        long expectedNumber = blockRepo.count() + 1;
        if (remoteBlock.getNumber() != expectedNumber) {
            throw new IllegalStateException("Block number mismatch");
        }

        // 3) Желательно проверить, что тот же самый голос (Vote) еще не записан,
        // например, по (userId, eventId) — иначе дублирование.
        // Но для простоты считаем, что каждая нода пишет Vote сразу в свою БД
        // и она уже есть. Если нет, придется проверять и записывать голос отдельно.

        return blockRepo.save(remoteBlock);
    }

    /**
     * Шлём сохраннённый блок на всех peers (POST /api/blockchain/peer-block).
     */
    private void broadcastToPeers(Block block) {
        RestTemplate rest = new RestTemplate();
        for (String peerUrl : peerUrls) {
            try {
                // отправляем JSON-представление блока на /api/blockchain/peer-block
                rest.postForEntity(peerUrl + "/api/blockchain/peer-block", block, Void.class);
            } catch (Exception ignored) {
                // Если одна из нод недоступна, пропускаем
            }
        }
    }

    // ───── Методы для остальных нужд ─────

    public List<Block> getAllBlocks() {
        return blockRepo.findAll(Sort.by("number"));
    }

    public Block getBlockByNumber(long num) {
        return blockRepo.findByNumber(num).orElseThrow(() -> new IllegalArgumentException("Block not found"));
    }
}
