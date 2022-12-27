package org.fruit.monkey.javaparser;

import java.util.List;

public class JavaProjectParserTestHelper {
    public static List<JavaUnit> expectedJavaUnits() {
        return List.of(
                new JavaUnit("com.example.orderline.OrderLine",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\orderline\\OrderLine.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("String"), 7, 9),
                                     new MethodDeclaration("getProductNumber", List.of(), 11, 13),
                                     new MethodDeclaration("setProductNumber", List.of("String"), 15, 17)
                             )),

                new JavaUnit("com.example.OrderManager",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of(), 7, 8),
                                     new MethodDeclaration("submitOrder", List.of("List<OrderLine>", "BigDecimal"), 10, 13)
                             )),

                new JavaUnit("com.example.OrderManager.OrderFactoryImpl",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 17, 19)
                             )),

                new JavaUnit("com.example.OrderManager.Bill",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("String", "BigDecimal"), 23, 23),
                                     new MethodDeclaration("prepareBill", List.of("String", "BigDecimal"), 25, 27),
                                     new MethodDeclaration("prepareBill", List.of("BigDecimal"), 29, 31),
                                     new MethodDeclaration("prepareBill", List.of("String", "com.example.monetary.BigDecimal"), 33, 35)
                             )),

                new JavaUnit("com.example.OrderFactory",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderFactory.java",
                             List.of(
                                     new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 6, 6)
                             )),

                new JavaUnit("com.example.Order",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\Order.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("List<OrderLine>", "BigDecimal"), 6, 9)
                             ))
        );
    }
}
