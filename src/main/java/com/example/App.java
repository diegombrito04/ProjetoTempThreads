package com.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class App {
    private static final int NUM_EXPERIMENTS = 20;
    private static final int NUM_ROUNDS = 10;
    private static final String DATA_DIR = "data/";  // Diretório contendo os arquivos CSV
    private static final String OUTPUT_DIR = "output/";  // Diretório para salvar resultados

    public static void main(String[] args) {
        // Garantir que o diretório de saída exista
        new File(OUTPUT_DIR).mkdirs();

        for (int experimentNum = 1; experimentNum <= NUM_EXPERIMENTS; experimentNum++) {
            System.out.println("Iniciando o Experimento " + experimentNum);
            List<Long> executionTimes = new ArrayList<>();

            for (int round = 1; round <= NUM_ROUNDS; round++) {
                System.out.println("  Executando rodada " + round);

                long startTime = System.currentTimeMillis();

                // Executar o experimento conforme o número de threads
                runExperiment(experimentNum);

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                executionTimes.add(duration);

                System.out.println("  Rodada " + round + " concluída em " + duration + "ms");
            }

            // Calcular o tempo médio
            long averageTime = executionTimes.stream().mapToLong(Long::longValue).sum() / NUM_ROUNDS;
            System.out.println("Tempo médio do experimento " + experimentNum + ": " + averageTime + "ms");

            // Salvar os resultados no arquivo
            saveResults(experimentNum, executionTimes, averageTime);
        }
    }

    // Rodar o experimento baseado no número de threads
    private static void runExperiment(int experimentNum) {
        if (experimentNum == 1 || experimentNum == 11) {
            processWithoutThreads();
        } else if (experimentNum >= 2 && experimentNum <= 10) {
            int numThreads = getThreadCountForExperiment(experimentNum);
            processWithThreads(numThreads);
        } else if (experimentNum >= 12 && experimentNum <= 20) {
            int numThreads = getThreadCountForExperiment(experimentNum - 10);
            processWithCityAndYearThreads(numThreads);
        }
    }

    // Versão sem threads (sequencial)
    private static void processWithoutThreads() {
        try {
            Files.list(Paths.get(DATA_DIR)).forEach(filePath -> {
                CityTemperatureProcessor processor = new CityTemperatureProcessor(filePath);
                processor.process();  // Processa o arquivo de forma sequencial
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Versão com múltiplas threads para as cidades
    private static void processWithThreads(int numThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        try {
            List<Path> files = Files.list(Paths.get(DATA_DIR)).collect(Collectors.toList());

            for (Path file : files) {
                executor.submit(() -> {
                    CityTemperatureProcessor processor = new CityTemperatureProcessor(file);
                    processor.process();  // Processa o arquivo CSV em threads
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Versão com múltiplas threads para cidades e anos
    private static void processWithCityAndYearThreads(int numCityThreads) {
        ExecutorService cityExecutor = Executors.newFixedThreadPool(numCityThreads);
        try {
            List<Path> files = Files.list(Paths.get(DATA_DIR)).collect(Collectors.toList());

            for (Path file : files) {
                cityExecutor.submit(() -> {
                    CityTemperatureProcessor processor = new CityTemperatureProcessor(file);
                    processor.processByYear();  // Processa o arquivo CSV por cidade e ano em threads
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cityExecutor.shutdown();
            try {
                cityExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Função para definir o número de threads de acordo com o experimento
    private static int getThreadCountForExperiment(int experimentNum) {
        switch (experimentNum) {
            case 2: return 2;
            case 3: return 4;
            case 4: return 8;
            case 5: return 16;
            case 6: return 32;
            case 7: return 64;
            case 8: return 80;
            case 9: return 160;
            case 10: return 320;
            default: return 1;
        }
    }

    // Função para salvar os resultados no arquivo
    private static void saveResults(int experimentNum, List<Long> executionTimes, long averageTime) {
        String fileName = OUTPUT_DIR + "versao_" + experimentNum + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Long time : executionTimes) {
                writer.write("Rodada: " + time + "ms\n");
            }
            writer.write("Tempo médio: " + averageTime + "ms\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
