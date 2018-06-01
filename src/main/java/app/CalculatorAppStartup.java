package app;

import common.OperationServiceFilter;
import common.StatisticServiceFilter;
import controller.Calculator;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceConfiguration;
import services.management.StatisticServiceImpl;
import services.operations.*;

import java.util.Collections;
import java.util.Scanner;

public class CalculatorAppStartup {
    public static void main(String[] args) {

        Ignition.setClientMode(true);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setUserAttributes(Collections.singletonMap("operation.service.node", true));
        Ignite ignite = Ignition.start(cfg);

        System.out.println("Client node has connected to the cluster");

        ignite.createCache("operations");

        deployServices(ignite);

        OperationService addSvc = ignite.services().serviceProxy("AdditionService", OperationService.class, /*not-sticky*/false);
        OperationService subSvc = ignite.services().serviceProxy("SubtractionService", OperationService.class, /*not-sticky*/false);
        OperationService mltSvc = ignite.services().serviceProxy("MultiplicationService", OperationService.class, /*not-sticky*/false);
        OperationService divSvc = ignite.services().serviceProxy("DivisionService", OperationService.class, /*not-sticky*/false);

        Calculator calculator = new Calculator(addSvc, subSvc, mltSvc, divSvc);

        Scanner scaner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter expression:");
            String current = scaner.nextLine();
            if (current.equals(""))
                break;
            System.out.println("\nResult = " + calculator.calculate(current));
        }
    }

    public static void deployOperationService(String name, Service service, Ignite ignite) {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setService(service);
        cfg.setName(name);
        cfg.setNodeFilter(new OperationServiceFilter(name));
        cfg.setMaxPerNodeCount(1);
        ignite.services().deploy(cfg);
    }

    public static void deployStatisticService(Ignite ignite) {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setService(new StatisticServiceImpl());
        cfg.setName("StatisticService");
        cfg.setNodeFilter(new StatisticServiceFilter());
        cfg.setMaxPerNodeCount(1);
        cfg.setTotalCount(1);
        ignite.services().deploy(cfg);
    }

    public static void deployServices(Ignite ignite) {
        deployStatisticService(ignite);
        deployOperationService("AdditionService", new AdditionService(), ignite);
        deployOperationService("SubtractionService", new SubtractionService(), ignite);
        deployOperationService("MultiplicationService", new MultiplicationService(), ignite);
        deployOperationService("DivisionService", new DivisionService(), ignite);
    }
}
