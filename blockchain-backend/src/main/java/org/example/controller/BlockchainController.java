// src/main/java/org/example/controller/BlockchainController.java
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Block;
import org.example.entity.User;
import org.example.entity.Vote;
import org.example.repository.VoteRepository;
import org.example.service.BlockchainService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BlockchainController {

    private final BlockchainService blockchainService;
    private final VoteRepository       voteRepository;
    private final UserService          userService;

    /**
     * POST /api/blockchain/vote?candidate={optionId}
     * Записывает голос (внутри себя блокированый объект Block).
     */
    @PostMapping("/vote")
    public ResponseEntity<Block> vote(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("candidate") Long optionId
    ) throws Exception {
        String email = userDetails.getUsername();         // ✅ это email, не username
        User user = userService.findUserByEmail(email);   // ✅ теперь правильно ищем

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Block block = blockchainService.addVoteToBlockchain(user, optionId);
        return ResponseEntity.ok(block);
    }

    /**
     * GET /api/blockchain/results
     * Возвращает список всех голосов (Vote). Благодаря аннотациям @JsonIgnoreProperties
     * мы разорвём циклы: у Vote сериализуется только event→(без опций/голосов) и user→(без votes).
     */
    @GetMapping("/results")
    public ResponseEntity<List<Vote>> getVotingResults() {
        List<Vote> votes = voteRepository.findAll();
        return ResponseEntity.ok(votes);
    }

    /**
     * GET /api/blockchain/blocks
     * Возвращает всю цепочку блоков (Block). Благодаря @JsonIgnoreProperties в Block
     * Jackson не будет развертывать снова event.options/event.votes/voter.votes.
     */
    @GetMapping("/blocks")
    public ResponseEntity<List<Block>> getBlockchain() {
        List<Block> blocks = blockchainService.getAllBlocks();
        return ResponseEntity.ok(blocks);
    }
}
