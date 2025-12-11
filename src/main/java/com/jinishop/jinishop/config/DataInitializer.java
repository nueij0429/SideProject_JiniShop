package com.jinishop.jinishop.config;

import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.domain.ProductStatus;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.product.repository.ProductRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.repository.StockRepository;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.domain.UserRole;
import com.jinishop.jinishop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final StockRepository stockRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            createUsers();
        }

        if (productRepository.count() == 0) {
            createProducts();
        }

        System.out.println("=== 데이터 초기화 완료 ===");
    }

    private void createUsers() {
        System.out.println("=== 사용자 데이터 생성 중 ===");

        List<User> users = List.of(
                User.builder().email("user1@test.com").password("1234").name("유저1").role(UserRole.USER).build(),
                User.builder().email("user2@test.com").password("1234").name("유저2").role(UserRole.USER).build(),
                User.builder().email("user3@test.com").password("1234").name("유저3").role(UserRole.USER).build(),
                User.builder().email("user4@test.com").password("1234").name("유저4").role(UserRole.USER).build(),
                User.builder().email("user5@test.com").password("1234").name("유저5").role(UserRole.USER).build()
        );

        userRepository.saveAll(users);
    }

    private void createProducts() {
        System.out.println("=== 상품 + 옵션 + 재고 데이터 생성 중 ===");

        for (int i = 1; i <= 1000; i++) {

            Product product = Product.builder()
                    .name("테스트 상품 " + i)
                    .description("테스트 상품 " + i + "의 설명")
                    .price(10000L + (i * 1000L))
                    .status(ProductStatus.ON_SALE)
                    .build();

            productRepository.save(product);

            // 각 상품마다 2개 옵션 생성
            ProductOption option1 = ProductOption.builder()
                    .product(product)
                    .color("Black")
                    .size("M")
                    .build();

            ProductOption option2 = ProductOption.builder()
                    .product(product)
                    .color("White")
                    .size("L")
                    .build();

            productOptionRepository.save(option1);
            productOptionRepository.save(option2);

            // 재고는 각 옵션마다 50개씩
            Stock stock1 = Stock.builder()
                    .productOption(option1)
                    .quantity(50)
                    .version(0)
                    .build();

            Stock stock2 = Stock.builder()
                    .productOption(option2)
                    .quantity(50)
                    .version(0)
                    .build();

            stockRepository.save(stock1);
            stockRepository.save(stock2);
        }
    }
}