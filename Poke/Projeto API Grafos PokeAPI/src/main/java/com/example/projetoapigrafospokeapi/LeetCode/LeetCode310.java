package com.example.projetoapigrafospokeapi.LeetCode;

import java.util.*;

public class LeetCode310 {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        // Caso base: se houver apenas um nó, ele é a raiz
        if (n == 1) {
            return Collections.singletonList(0);
        }

        // 1. Inicializar a lista de adjacência e o array de graus
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new HashSet<>());
        }

        int[] degree = new int[n];
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            adj.get(u).add(v);
            adj.get(v).add(u);
            degree[u]++;
            degree[v]++;
        }

        // 2. Encontrar as folhas iniciais (grau == 1)
        Queue<Integer> leaves = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] == 1) {
                leaves.offer(i);
            }
        }

        // 3. Descascar a árvore até sobrarem 2 ou menos nós
        int remainingNodes = n;
        while (remainingNodes > 2) {
            int size = leaves.size();
            remainingNodes -= size;

            for (int i = 0; i < size; i++) {
                int leaf = leaves.poll();

                // Para cada vizinho da folha removida
                for (int neighbor : adj.get(leaf)) {
                    degree[neighbor]--;
                    // Se o vizinho se tornou uma folha, vai para a próxima camada
                    if (degree[neighbor] == 1) {
                        leaves.offer(neighbor);
                    }
                }
            }
        }

        // 4. O que restou na fila são os nós centrais
        return new ArrayList<>(leaves);
    }
}