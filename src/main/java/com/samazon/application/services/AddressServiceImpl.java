package com.samazon.application.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.dto.addresses.AddressResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.AccessDeniedException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Address;
import com.samazon.application.models.User;
import com.samazon.application.repositories.AddressRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request, User user) {
        if (user.getId() == null) {
            throw new APIException("User must be persisted before adding an address");
        }
        boolean exists = addressRepository
                .existsByUserIdAndBuildingAndStreetAndCityAndStateAndCountryAndZipCodeAndIdNot(
                        user.getId(), request.getBuilding(), request.getStreet(), request.getCity(), request.getState(),
                        request.getCountry(), request.getZipCode(), null);
        if (exists) {
            throw new APIException("Address already exists for this user with the same details");
        }
        Address address = modelMapper.map(request, Address.class);
        System.out.println("Address: " + address);
        address.setUser(user);
        try {
            Address createdAddress = addressRepository.save(address);
            if (user.getAddresses() != null) {
                user.getAddresses().add(createdAddress);
            }
            return modelMapper.map(createdAddress, AddressResponse.class);
        } catch (Exception e) {
            throw new APIException("Address already exists for this user with the same details");
        }
    }

    @Override
    public List<AddressResponse> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();
    }

    @Override
    public List<AddressResponse> getAddressesByUser(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        return modelMapper.map(address, AddressResponse.class);
    }

    @Override
    public AddressResponse updateAddress(Long addressId, AddressRequest request, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to update this address");
        }
        if (addressRepository.existsByUserIdAndBuildingAndStreetAndCityAndStateAndCountryAndZipCodeAndIdNot(
                user.getId(), request.getBuilding(), request.getStreet(), request.getCity(), request.getState(),
                request.getCountry(), request.getZipCode(), addressId)) {
            throw new APIException("Address already exists for this user with the same details");
        }
        modelMapper.map(request, address);
        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress, AddressResponse.class);
    }

    @Override
    public Void deleteAddress(Long addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to delete this address");
        }
        addressRepository.delete(address);
        return null;
    }

}
