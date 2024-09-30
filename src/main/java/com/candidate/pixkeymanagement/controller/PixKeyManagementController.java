package com.candidate.pixkeymanagement.controller;

import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.dto.PixKeyUpdateRequestDTO;
import com.candidate.pixkeymanagement.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class PixKeyManagementController {

    private final RegisterKeyService registerKeyService;
    private final UpdateKeyService updateKeyService;
    private final SearchKeyByIdService searchKeyByIdService;
    private final SearchKeyByFilterService searchKeyByFilterService;
    private final DeleteKeyService deleteKeyService;

    @PostMapping
    private ResponseEntity<PixKeyResponseDTO> createPixKey(@Valid @RequestBody PixKeyRequestDTO pixKeyRequestDTO) {
        log.debug("Started POST/ pix key: {}", pixKeyRequestDTO);
        PixKeyResponseDTO pixKeyResponseDTO = registerKeyService.process(pixKeyRequestDTO);

        log.debug("Finished POST/ pix key: {}", pixKeyResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pixKeyResponseDTO);
    }

    @PatchMapping
    private ResponseEntity<PixKeyResponseDTO> updatePixKey(@Valid @RequestBody PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO) {
        log.debug("Started PATCH/ pix key: {}", pixKeyUpdateRequestDTO);
        PixKeyResponseDTO pixKeyResponseDTO = updateKeyService.process(pixKeyUpdateRequestDTO);

        log.debug("Finished PATCH/ pix key: {}", pixKeyResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @GetMapping("/{id}")
    private ResponseEntity<PixKeyResponseDTO> getPixKeyById(@PathVariable UUID id) {
        log.debug("Started GET/{id} pix key");
        PixKeyResponseDTO pixKeyResponseDTO = searchKeyByIdService.process(id);

        log.debug("Finished GET/{id} pix key. {}", pixKeyResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @GetMapping("/filter")
    private ResponseEntity<List<PixKeyResponseDTO>> getPixKeyByFilter(@RequestParam(value = "tipoChave", required = false) String keyType,
                                                                      @RequestParam(value = "numeroAgencia", required = false) String agencyNumber,
                                                                      @RequestParam(value = "numeroConta", required = false) String accountNumber,
                                                                      @RequestParam(value = "nomeCorrentista", required = false) String accountHolderFirstName) {
        log.debug("Started GET/filter pix key");
        List<PixKeyResponseDTO> pixKeyResponseDTO = searchKeyByFilterService.process(keyType, agencyNumber, accountNumber, accountHolderFirstName);

        log.debug("Finished GET/filter pix key");
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<PixKeyResponseDTO> deletePixKeyById(@PathVariable UUID id) {
        log.debug("Started DELETE/{id} pix key");
        PixKeyResponseDTO pixKeyResponseDTO = deleteKeyService.process(id);

        log.debug("Finished DELETE/{id} pix key");
        return ResponseEntity.status(HttpStatus.OK).body(pixKeyResponseDTO);
    }
}
