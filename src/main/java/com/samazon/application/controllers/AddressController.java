package com.samazon.application.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.dto.addresses.AddressResponse;
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

    @PostMapping("/user/addresses")
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request) {
        User user = authUtil.getCurrentUser();
        AddressResponse createdAddressResponse = addressService.createAddress(request, user);
        return new ResponseEntity<>(createdAddressResponse, HttpStatus.CREATED);
    }

    @GetMapping("/admin/address-audit")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        List<AddressResponse> allAddressesResponse = addressService.getAllAddresses();
        return new ResponseEntity<>(allAddressesResponse, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        User user = authUtil.getCurrentUser();
        List<AddressResponse> userAddressesResponse = addressService.getAddressesByUser(user);
        return new ResponseEntity<>(userAddressesResponse, HttpStatus.OK);
    }

    @GetMapping("/user/address/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long addressId) {
        AddressResponse addressResponse = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressResponse, HttpStatus.OK);
    }

    @PutMapping("/user/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        User user = authUtil.getCurrentUser();
        AddressResponse updatedAddressResponse = addressService.updateAddress(addressId, request, user);
        return new ResponseEntity<>(updatedAddressResponse, HttpStatus.OK);
    }

    @DeleteMapping("/user/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        User user = authUtil.getCurrentUser();
        addressService.deleteAddress(addressId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
