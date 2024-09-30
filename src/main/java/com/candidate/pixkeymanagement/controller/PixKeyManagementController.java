package com.candidate.pixkeymanagement.controller;

import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.dto.PixKeyUpdateRequestDTO;
import com.candidate.pixkeymanagement.service.RegisterKeyService;
import com.candidate.pixkeymanagement.service.SearchKeyByFilterService;
import com.candidate.pixkeymanagement.service.SearchKeyByIdService;
import com.candidate.pixkeymanagement.service.UpdateKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class PixKeyManagementController {

    private final RegisterKeyService registerKeyService;
    private final UpdateKeyService updateKeyService;
    private final SearchKeyByIdService searchKeyByIdService;
    private final SearchKeyByFilterService searchKeyByFilterService;

    @PostMapping
    private ResponseEntity<PixKeyResponseDTO> createPixKey(@Valid @RequestBody PixKeyRequestDTO pixKeyRequestDTO) {
        PixKeyResponseDTO pixKeyResponseDTO = registerKeyService.process(pixKeyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pixKeyResponseDTO);
    }

    @PatchMapping
    private ResponseEntity<PixKeyResponseDTO> updatePixKey(@RequestBody PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO) {
        PixKeyResponseDTO pixKeyResponseDTO = updateKeyService.process(pixKeyUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @GetMapping("/{id}")
    private ResponseEntity<PixKeyResponseDTO> getPixKeyById(@PathVariable UUID id) {
        PixKeyResponseDTO pixKeyResponseDTO = searchKeyByIdService.process(id);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @GetMapping("/filter")
    private ResponseEntity<List<PixKeyResponseDTO>> getPixKeyByFilter(@RequestParam(value = "tipoChave", required = false) String keyType,
                                                                      @RequestParam(value = "numeroAgencia", required = false) String agencyNumber,
                                                                      @RequestParam(value = "numeroConta", required = false) String accountNumber,
                                                                      @RequestParam(value = "nomeCorrentista", required = false) String accountHolderFirstName) {

        List<PixKeyResponseDTO> pixKeyResponseDTO = searchKeyByFilterService.process(keyType, agencyNumber, accountNumber, accountHolderFirstName);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @DeleteMapping
    private void deletePixKey(@RequestBody PixKeyRequestDTO pixKeyRequestDTO) {

    }

}
