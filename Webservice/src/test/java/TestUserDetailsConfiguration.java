import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.security.WarehouseCompanyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * Created by Aleksandr on 29.04.2017.
 */
@Configuration
public class TestUserDetailsConfiguration {

    public UserDetails userDetails() {
        User user = new User();
        user.setId(Long.valueOf(1));
        user.setLogin("1");
        user.setPassword("1");
        Role role = new Role();
        role.setRole(UserRoleEnum.ROLE_SUPERVISOR.toString());
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(role);
        WarehouseCompany company = new WarehouseCompany();
        company.setIdWarehouseCompany(Long.valueOf(1));
        user.setWarehouseCompany(company);
        user.setRoles(roles);
        return new WarehouseCompanyUserDetails(user);
    }

    @Bean
    public UserDetailsService userDetailsService(){
        WarehouseCompanyUserDetailsService userDetailsService = new WarehouseCompanyUserDetailsService(){
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDetails();
            }
        };

        return userDetailsService;
    }
}
