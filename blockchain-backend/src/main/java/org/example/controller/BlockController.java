// src/main/java/org/example/controller/BlockController.java
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Block;
import org.example.service.BlockchainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BlockController {

    private final BlockchainService blockchainService;

    /** GET /api/blocks — вернуть всю цепочку блоков */
    @GetMapping
    public ResponseEntity<List<Block>> getAllBlocks() {
        return ResponseEntity.ok(blockchainService.getAllBlocks());
    }

    /** GET /api/blocks/{number} — вернуть конкретный блок по номеру */
    @GetMapping("/{number}")
    public ResponseEntity<Block> getBlockByNumber(@PathVariable Long number) {
        Block b = blockchainService.getBlockByNumber(number);
        return ResponseEntity.ok(b);
    }
}
