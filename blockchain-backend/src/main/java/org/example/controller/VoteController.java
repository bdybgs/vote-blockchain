package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.BlockDto;
import org.example.dto.request.VoteRequest;
import org.example.entity.Block;
import org.example.entity.User;
import org.example.service.BlockchainService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vote")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class VoteController {

    private final BlockchainService blockchain;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlockDto vote(@RequestBody VoteRequest req,
                         @AuthenticationPrincipal UserDetails ud) throws Exception {

        User u = userService.findUserByUsername(ud.getUsername());
        Block b = blockchain.addVoteToBlockchain(u, req.optionId());
        return BlockDto.of(b);
    }
}
