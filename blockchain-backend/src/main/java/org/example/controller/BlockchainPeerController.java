// src/main/java/org/example/controller/BlockchainPeerController.java
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Block;
import org.example.service.BlockchainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // доступно всем нодам
public class BlockchainPeerController {

    private final BlockchainService blockchainService;

    /**
     * Эндпоинт для приёма блока от другой ноды-валидатора.
     * Проверяем подпись, если всё ок — сохраняем.
     */
    @PostMapping("/peer-block")
    public ResponseEntity<Void> receivePeerBlock(@RequestBody Block block) {
        try {
            blockchainService.receiveBlockFromPeer(block);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
