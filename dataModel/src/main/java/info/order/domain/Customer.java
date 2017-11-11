package info.order.domain;


import javax.persistence.*;
import java.util.List;


@NamedQueries(value = {
	@NamedQuery(name = "getCustomersByLastName",
			query = "SELECT c FROM Customer c WHERE c.lastName LIKE :lastName"),
	@NamedQuery(name = "getCustomersByFirstName",
			query = "SELECT c FROM Customer c WHERE c.firstName LIKE :firstName"),
	}
)

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Embedded
    private Address address;
    @OneToMany(mappedBy = "customer")
    private List<CustomerOrder> orders;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<CustomerOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<CustomerOrder> orders) {
        this.orders = orders;
    }
}
