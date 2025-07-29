package com.samazon.application.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.AddressDTO;
import com.samazon.application.models.User;
import com.samazon.application.services.AddressService;
import com.samazon.application.utils.AuthUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController()
@RequestMapping("/api")
@AllArgsConstructor
public class AddressController {
    private final AuthUtil authUtil;
    private final AddressService addressService;

    @GetMapping("/admin/addresses/all")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOs = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @GetMapping("/admin/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.getCurrentUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        User user = authUtil.getCurrentUser();
        List<AddressDTO> addressDTOs = addressService.getAddressesByUser(user);
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        User user = authUtil.getCurrentUser();
        addressService.deleteAddress(addressId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
