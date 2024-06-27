package com.PreciousCoding.Unit.IntegrationTesting.Customer;

import com.PreciousCoding.Unit.IntegrationTesting.exception.CustomerEmailUnavailableException;
import com.PreciousCoding.Unit.IntegrationTesting.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCustomers() {
        List<Customer> customers = List.of(new Customer(), new Customer());
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getCustomers();

        assertThat(result).isEqualTo(customers);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_CustomerExists() {
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertThat(result).isEqualTo(customer);
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(1L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 1 doesn't found");
    }

    @Test
    void createCustomer_EmailUnavailable() {
        CreateCustomerRequest request = new CreateCustomerRequest("John", "john@example.com", "123 Street");
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new Customer()));

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email john@example.com unavailable.");
    }

    @Test
    void createCustomer_Success() {
        CreateCustomerRequest request = new CreateCustomerRequest("John", "john@example.com", "123 Street");
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        customerService.createCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo("John");
        assertThat(capturedCustomer.getEmail()).isEqualTo("john@example.com");
        assertThat(capturedCustomer.getAddress()).isEqualTo("123 Street");
    }

    @Test
    void updateCustomer_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(1L, "Jane", "jane@example.com", "456 Avenue"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 1 doesn't found");
    }

    @Test
    void updateCustomer_EmailUnavailable() {
        Customer customer = new Customer();
        customer.setEmail("john@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(new Customer()));

        assertThatThrownBy(() -> customerService.updateCustomer(1L, "Jane", "jane@example.com", "456 Avenue"))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email \"jane@example.com\" unavailable to update");
    }

    @Test
    void updateCustomer_Success() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setEmail("john@example.com");
        customer.setAddress("123 Street");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

        customerService.updateCustomer(1L, "Jane", "jane@example.com", "456 Avenue");

        assertThat(customer.getName()).isEqualTo("Jane");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");
        assertThat(customer.getAddress()).isEqualTo("456 Avenue");
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_CustomerNotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(1L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 1 doesn't exist.");
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }
}
