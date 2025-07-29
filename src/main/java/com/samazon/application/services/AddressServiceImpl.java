package com.samazon.application.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.AddressDTO;
import com.samazon.application.exceptions.AccessDeniedException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Address;
import com.samazon.application.models.User;
import com.samazon.application.repositories.AddressRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public void deleteAddress(Long addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to delete this address");
        }
        addressRepository.delete(address);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to update this address");
        }
        modelMapper.map(addressDTO, address);
        address.setId(addressId);
        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }
}
