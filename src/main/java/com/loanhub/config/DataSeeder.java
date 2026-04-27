package com.loanhub.config;

import com.loanhub.entity.User;
import com.loanhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
            System.out.println("\n✅ Default users seeded successfully!");
            System.out.println("   admin@loanhub.com    / Ponish@2128  (admin — gannamaneni ponish)");
            System.out.println("   lender@loanhub.com   / lender123    (lender)");
            System.out.println("   borrower@loanhub.com / borrower123  (borrower)");
            System.out.println("   analyst@loanhub.com  / analyst123   (analyst)\n");
        }
    }

    private void seedUsers() {
        // Admin — fixed credentials: name=gannamaneni ponish / password=Ponish@2128
        userRepository.save(User.builder()
                .name("gannamaneni ponish")
                .email("admin@loanhub.com")
                .password(passwordEncoder.encode("Ponish@2128"))
                .phone("9000000001")
                .role(User.Role.admin)
                .status(User.Status.active)
                .kycStatus(User.KycStatus.verified)
                .dob("1980-01-01")
                .build());

        // Lender
        userRepository.save(User.builder()
                .name("John Lender")
                .email("lender@loanhub.com")
                .password(passwordEncoder.encode("lender123"))
                .phone("9000000002")
                .role(User.Role.lender)
                .status(User.Status.active)
                .kycStatus(User.KycStatus.verified)
                .dob("1985-06-15")
                .panCard("ABCDE1234F")
                .aadhaarCard("123456789012")
                .annualIncome("1200000")
                .build());

        // Borrower
        userRepository.save(User.builder()
                .name("Jane Borrower")
                .email("borrower@loanhub.com")
                .password(passwordEncoder.encode("borrower123"))
                .phone("9000000003")
                .role(User.Role.borrower)
                .status(User.Status.active)
                .kycStatus(User.KycStatus.incomplete)
                .dob("1992-03-22")
                .panCard("PQRST5678G")
                .aadhaarCard("987654321098")
                .annualIncome("600000")
                .build());

        // Analyst
        userRepository.save(User.builder()
                .name("Analyst Pro")
                .email("analyst@loanhub.com")
                .password(passwordEncoder.encode("analyst123"))
                .phone("9000000004")
                .role(User.Role.analyst)
                .status(User.Status.active)
                .kycStatus(User.KycStatus.verified)
                .dob("1990-11-08")
                .panCard("LMNOP9012H")
                .aadhaarCard("456789012345")
                .education("MBA Finance")
                .build());
    }
}
