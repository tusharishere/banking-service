package com.banking.service.impl;

import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.CustomerDTO;
import com.banking.repository.CustomerRepository;
import com.banking.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private ModelMapper modelMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating customer with email: {}", customerDTO.getEmail());
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            log.warn("Email already exists: {}", customerDTO.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        if (customerRepository.existsByIdentificationNumber(customerDTO.getIdentificationNumber())) {
            log.warn("Identification number already exists: {}", customerDTO.getIdentificationNumber());
            throw new IllegalArgumentException("Identification number already exists");
        }

        Customer customer = mapToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return mapToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        log.info("Fetching customer by ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });
        log.info("Customer fetched successfully with ID: {}", id);
        return mapToDTO(customer);
    }

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        log.info("Fetching customer by email: {}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Customer not found with email: {}", email);
                    return new ResourceNotFoundException("Customer not found with email: " + email);
                });
        log.info("Customer fetched successfully with email: {}", email);
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        log.info("Total customers fetched: {}", customers.size());
        return customers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });
        if (!customer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            log.warn("Email already exists: {}", customerDTO.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        if (!customer.getIdentificationNumber().equals(customerDTO.getIdentificationNumber()) &&
                customerRepository.existsByIdentificationNumber(customerDTO.getIdentificationNumber())) {
            log.warn("Identification number already exists: {}", customerDTO.getIdentificationNumber());
            throw new IllegalArgumentException("Identification number already exists");
        }

        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setAddress(customerDTO.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", id);
        return mapToDTO(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            log.error("Customer not found with ID: {}", id);
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.info("Checking existence of customer by email: {}", email);
        return customerRepository.existsByEmail(email);
    }

    private CustomerDTO mapToDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    private Customer mapToEntity(CustomerDTO dto) {
        return modelMapper.map(dto, Customer.class);
    }
}
