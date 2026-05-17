# Damas 6x6 com IA

Este projeto implementa um jogo de damas 6x6 com interface grafica em Swing e uma IA baseada em busca adversarial. A IA analisa jogadas futuras, simula respostas do adversario e escolhe a jogada com maior pontuacao segundo uma heuristica.

O objetivo deste README e explicar a logica do projeto: representacao do tabuleiro, geracao de movimentos, construcao da arvore, algoritmo Minimax com poda alfa-beta e funcao de avaliacao heuristica.

## Visao geral da IA

A IA funciona em quatro etapas principais:

1. Recebe o estado atual do tabuleiro.
2. Gera todas as jogadas legais possiveis para a IA.
3. Para cada jogada, constroi uma arvore de possibilidades alternando entre IA e humano.
4. Avalia os estados futuros com uma heuristica e escolhe a jogada com melhor score.

Em termos de Inteligencia Artificial, este e um problema de busca em jogo de soma zero:

- A IA tenta maximizar a pontuacao.
- O humano tenta minimizar a pontuacao da IA.
- Cada no da arvore representa um estado possivel do tabuleiro apos uma jogada.
- A heuristica estima o quao bom e um estado quando nao vale a pena, ou nao da tempo, expandir a arvore ate o fim real da partida.

## Arquivos principais

- `src/main/logicGame/Controlador.java`: controla turnos, chama a IA e aplica jogadas no tabuleiro real.
- `src/main/ai/Tree.java`: implementa a busca Minimax com poda alfa-beta.
- `src/main/ai/Simulador.java`: gera estados filhos simulando jogadas sem alterar diretamente a interface.
- `src/main/ai/Node.java`: representa um no da arvore, contendo origem, destino, estado resultante e score.
- `src/main/ai/avaliacoes/Avaliador.java`: interface comum para funcoes heuristicas.
- `src/main/ai/avaliacoes/AvaliacaoOtimizada.java`: heuristica principal usada atualmente pela IA.
- `src/main/ai/avaliacoes/AvaliacaoPosicional.java`: heuristica alternativa com foco em posicao e avanco.
- `src/main/ai/avaliacoes/ContaPecas.java`: heuristica simples baseada apenas em material.
- `src/main/entidades/Tabuleiro.java`: regras basicas de movimento, promocao e representacao do tabuleiro.
- `src/main/entidades/GeradorCapturas.java`: encontra capturas obrigatorias e sequencias de captura multipla.
- `src/main/logicGame/Tradutor.java`: converte coordenadas da matriz 6x6 para o vetor compacto de 18 posicoes.

## Representacao do tabuleiro

Embora a interface mostre uma matriz 6x6, a IA usa um vetor compacto de 18 posicoes:

```text
Linha 0: indices  0,  1,  2
Linha 1: indices  3,  4,  5
Linha 2: indices  6,  7,  8
Linha 3: indices  9, 10, 11
Linha 4: indices 12, 13, 14
Linha 5: indices 15, 16, 17
```

Isso funciona porque, em um tabuleiro de damas, apenas metade das casas e jogavel. Em um tabuleiro 6x6 existem 36 casas visuais, mas apenas 18 casas validas.

Vantagens dessa representacao:

- Menos memoria por estado.
- Menos casas para percorrer na avaliacao.
- Simulacao mais rapida dentro da arvore.
- A heuristica consegue avaliar o tabuleiro com apenas 18 iteracoes.

O `Tradutor` faz a conversao:

```java
indice = (linha * 3) + (coluna / 2);
```

E tambem converte o indice de volta para linha e coluna quando a interface precisa pintar ou mover uma peca.

## Pecas e valores internos

As pecas sao representadas por caracteres:

```text
'b' = peca branca
'p' = peca preta
'B' = dama branca
'P' = dama preta
' ' = casa vazia
'$' = casa invalida
```

A classe `Peca` tambem define as direcoes permitidas:

- Brancas comuns andam para cima: `{-1, -1}` e `{-1, 1}`.
- Pretas comuns andam para baixo: `{1, -1}` e `{1, 1}`.
- Damas andam nas quatro diagonais.

## Fluxo quando chega a vez da IA

O fluxo comeca em `Controlador.executarJogadaIA()`:

1. Confere se realmente e a vez da IA.
2. Cria uma `Tree` com profundidade definida pelo nivel selecionado na tela inicial.
3. Usa `AvaliacaoOtimizada` como funcao heuristica.
4. Chama `decidirMelhorJogada(tabuleiro)`.
5. Recebe o melhor `Node`.
6. Importa o vetor resultante para o tabuleiro real.
7. Passa o turno para o humano.

Trecho conceitual:

```java
Tree engine = new Tree(nivelIA, jogadorIA, new AvaliacaoOtimizada());
Node melhorMovimento = engine.decidirMelhorJogada(tabuleiro);
tabuleiro.importarVetor(melhorMovimento.getEstado());
```

Ou seja: a IA nao move a peca diretamente na interface. Ela escolhe um estado futuro. Depois o controlador substitui o estado atual do tabuleiro pelo estado escolhido.

## O que e um Node

Um `Node` representa uma jogada ou um estado dentro da arvore.

Ele guarda:

- `origemIndice`: casa de onde a peca saiu.
- `destinoIndice`: casa para onde a peca foi.
- `estado`: vetor de 18 posicoes apos a jogada.
- `score`: nota heuristica daquele estado.
- `filhos`: proximos estados possiveis.
- `turno`: indica se o proximo nivel da arvore e de maximizacao ou minimizacao.
- `captura`, `pecasCapturadasIndices`, `caminhoIndices`: metadados usados para capturas e feedback visual.

Na apresentacao, uma boa forma de explicar e:

> Cada no e uma fotografia do tabuleiro depois de uma jogada. A arvore e o conjunto de fotografias futuras que a IA consegue imaginar.

## Geracao de movimentos

A geracao de movimentos acontece em `Simulador.gerarEstadosFilhos()`.

Ela recebe:

- O vetor atual do tabuleiro.
- O jogador que vai jogar naquele nivel.
- Se aquele nivel e de maximizacao ou minimizacao.

Primeiro, o simulador verifica capturas obrigatorias:

```java
List<MovimentoCaptura> capturas = tab.obterCapturasObrigatorias(atual);
```

Se existirem capturas, ele gera apenas movimentos de captura. Isso respeita a regra das damas: captura e obrigatoria.

Se nao existirem capturas, ele gera movimentos simples:

```java
List<Node> simples = tab.obterMovimentosSimples(atual);
```

Portanto, a ordem da logica e:

```text
Tem captura?
  Sim -> gerar somente capturas
  Nao -> gerar movimentos simples
```

## Capturas obrigatorias e multiplas

A classe `GeradorCapturas` busca as melhores sequencias de captura.

Ela percorre as 18 casas validas do vetor. Para cada peca do jogador atual, tenta construir sequencias de captura usando recursao.

Durante a recursao:

1. Verifica se existe uma peca inimiga na diagonal.
2. Verifica se a casa depois da inimiga esta vazia.
3. Simula a captura.
4. Adiciona a peca capturada na lista.
5. Adiciona o destino no caminho.
6. Continua buscando novas capturas a partir da nova posicao.
7. Desfaz a simulacao com backtracking para testar outros caminhos.

O backtracking e importante porque a mesma peca pode ter mais de uma rota de captura. A IA precisa testar todas sem corromper o tabuleiro original.

No final, o gerador guarda apenas os movimentos com maior numero de capturas:

```text
Se uma rota captura 1 peca e outra captura 2,
a rota com 2 capturas e considerada melhor.
```

Isso implementa a regra de prioridade da maior captura.

## Construcao da arvore

A arvore e construida em `Tree.decidirMelhorJogada()`.

Primeiro sao gerados os filhos iniciais da raiz:

```java
List<Node> filhosIniciais = Simulador.gerarEstadosFilhos(estadoVetor, jogadorIA, true);
```

Cada filho representa uma jogada possivel da IA no estado atual.

Depois, para cada filho, a IA chama o algoritmo alfa-beta:

```java
alfabeta(filho, alturaMaxima - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, filho.isTurno());
```

A profundidade vem do slider da tela inicial. Quanto maior a profundidade:

- Mais jogadas futuras a IA analisa.
- Melhor tende a ser a decisao.
- Maior e o custo computacional.

Exemplo com profundidade 4:

```text
Nivel 0: estado atual
Nivel 1: jogadas da IA
Nivel 2: respostas do humano
Nivel 3: respostas da IA
Nivel 4: respostas do humano
```

Quando a profundidade chega a zero, a arvore para de expandir e chama a heuristica.

## Minimax

O Minimax e usado porque o jogo tem dois lados com objetivos opostos.

- Nos niveis da IA, o algoritmo escolhe o maior score.
- Nos niveis do humano, o algoritmo escolhe o menor score, porque assume que o humano tambem tentara jogar bem.

Ideia:

```text
IA: escolhe o melhor cenario para ela
Humano: escolhe o pior cenario para a IA
IA: escolhe novamente o melhor cenario possivel
...
```

Isso evita uma IA ingenua que olha apenas uma jogada a frente. Ela tenta antecipar a resposta do adversario.

Pseudo-codigo simplificado:

```text
alfabeta(no, profundidade, alpha, beta, maximizando):
    se profundidade == 0:
        retorna heuristica(no)

    se maximizando:
        melhor = -infinito
        para cada filho:
            valor = alfabeta(filho, profundidade - 1, alpha, beta, false)
            melhor = max(melhor, valor)
            alpha = max(alpha, valor)
            se beta <= alpha:
                parar busca neste ramo
        retorna melhor

    se minimizando:
        melhor = +infinito
        para cada filho:
            valor = alfabeta(filho, profundidade - 1, alpha, beta, true)
            melhor = min(melhor, valor)
            beta = min(beta, valor)
            se beta <= alpha:
                parar busca neste ramo
        retorna melhor
```

## Poda alfa-beta

A poda alfa-beta e uma otimizacao do Minimax.

Ela nao muda a resposta final em relacao ao Minimax puro. Ela apenas evita analisar ramos que nao podem mais alterar a decisao.

Significado:

- `alpha`: melhor valor que a IA maximizadora ja encontrou.
- `beta`: melhor valor que o jogador minimizador ja encontrou.

Quando `beta <= alpha`, o algoritmo sabe que aquele ramo nao sera escolhido, entao interrompe a busca nele.

Exemplo para apresentar:

> Se a IA ja encontrou uma jogada que garante 200 pontos, e em outro ramo o humano ja consegue forcar um resultado pior que isso, nao faz sentido continuar explorando esse ramo. Ele nao sera escolhido.

Beneficio:

- Reduz o numero de estados avaliados.
- Permite usar profundidades maiores.
- Mantem o mesmo resultado do Minimax, quando a ordenacao dos filhos e a mesma.

## Heuristica

A heuristica e a funcao que da uma nota para um estado do tabuleiro.

Ela e necessaria porque a IA nao joga a partida inteira ate o final em cada decisao. Em vez disso, ela olha algumas jogadas a frente e estima se o estado final daquela simulacao e bom ou ruim.

A interface `Avaliador` define:

```java
int avaliar(char[] vetor, Jogador jogadorIA);
```

Contrato:

- Score positivo: vantagem da IA.
- Score negativo: vantagem do humano.
- Score perto de zero: jogo equilibrado.

## Heuristica principal: AvaliacaoOtimizada

A IA atual usa `AvaliacaoOtimizada`.

Ela combina tres criterios:

1. Material: quantidade e tipo das pecas.
2. Posicao: casas centrais e estrategicas valem mais.
3. Defesa da base: pecas comuns protegendo a linha inicial recebem bonus.

Pesos:

```java
PESO_PECA = 100
PESO_DAMA = 300
PESO_POSICAO = 5
```

Interpretacao:

- Uma peca comum vale 100.
- Uma dama vale 300.
- A posicao adiciona um bonus pequeno, mas relevante.
- Uma peca comum defendendo a base da IA ganha bonus de 30.

Tabela posicional usada:

```text
Linha 0: 1, 1, 1
Linha 1: 1, 3, 3
Linha 2: 4, 5, 2
Linha 3: 2, 5, 4
Linha 4: 3, 3, 1
Linha 5: 1, 1, 1
```

Como `PESO_POSICAO = 5`, uma casa com peso 5 adiciona 25 pontos.

Exemplo:

```text
Peca comum em casa central de peso 5:
valor = 100 + (5 * 5)
valor = 125

Dama em casa de peso 3:
valor = 300 + (3 * 5)
valor = 315
```

Depois, a heuristica soma as pecas da IA e subtrai as pecas do humano:

```text
score = pontos_da_IA - pontos_do_humano
```

Exemplo:

```text
IA:
2 pecas comuns bem posicionadas = 250
1 dama = 315
total IA = 565

Humano:
3 pecas comuns = 330
total humano = 330

score = 565 - 330 = 235
```

Score positivo indica vantagem da IA.

## Por que a dama vale mais

A dama vale mais porque tem maior mobilidade. Ela pode andar nas quatro diagonais, enquanto a peca comum tem direcao limitada. Isso aumenta o numero de movimentos possiveis e a capacidade de atacar ou escapar.

No projeto:

```text
peca comum = 100
dama = 300
```

Ou seja, uma dama equivale aproximadamente a tres pecas comuns.

## Por que existe bonus posicional

Contar apenas pecas nao e suficiente. Duas posicoes com o mesmo numero de pecas podem ter qualidades muito diferentes.

O bonus posicional incentiva:

- Ocupar casas centrais.
- Manter pecas em regioes com maior mobilidade.
- Evitar avaliar como iguais estados que tem o mesmo material, mas posicoes diferentes.

Isso torna a IA menos mecanica. Ela nao escolhe apenas capturar ou acumular material; ela tambem prefere posicoes melhores.

## Heuristicas alternativas

O projeto tem outras heuristicas implementadas.

### ContaPecas

E a mais simples.

Ela considera:

- Peca comum: 10 pontos.
- Dama: 30 pontos.

Nao considera posicao, avanco ou controle do centro.

Uso didatico:

> Serve como baseline. E facil de explicar, mas joga de forma menos estrategica.

### AvaliacaoPosicional

Combina:

- Valor material.
- Bonus de posicao.
- Bonus de avanco para pecas comuns.

Ela incentiva pecas comuns a caminharem em direcao a promocao:

- Brancas querem chegar na linha 0.
- Pretas querem chegar na linha 5.

Uso didatico:

> Mostra uma evolucao em relacao a contar pecas, porque considera o objetivo de virar dama.

### AvaliacaoOtimizada

E a usada atualmente.

Ela foi pensada para ser rapida e adequada ao vetor compacto:

- Percorre apenas 18 posicoes.
- Usa tabela de pesos em vetor.
- Usa valores maiores para diferenciar material e dama.
- Adiciona bonus defensivo.

## Captura multipla e turno

Em damas, uma peca pode continuar capturando na mesma jogada.

O `Simulador` trata isso verificando:

```java
boolean podeContinuar = gc.podeContinuarCapturando(...);
filho.setTurno(podeContinuar ? isMaximizingIA : !isMaximizingIA);
```

Se ainda pode capturar, o turno logico permanece com o mesmo jogador. Se nao pode, alterna para o adversario.

Isso e importante porque a arvore precisa representar corretamente sequencias de captura.

## Estados terminais

Dentro de `Tree.alfabeta()`, se nao existem sucessores:

```java
return isMaximizing ? -10000 : 10000;
```

Interpretacao:

- Se e a vez da IA e ela nao tem jogadas, isso e ruim para a IA: `-10000`.
- Se e a vez do humano e ele nao tem jogadas, isso e bom para a IA: `10000`.

Esses valores grandes fazem o algoritmo preferir vitorias reais em vez de pequenas vantagens posicionais.

## Exemplo completo de decisao

Imagine que a IA tem tres jogadas possiveis:

```text
Jogada A: captura uma peca, mas perde uma dama depois.
Jogada B: nao captura agora, mas evita uma armadilha.
Jogada C: promove uma peca em dois turnos.
```

Uma IA gulosa escolheria a Jogada A porque captura imediatamente.

A IA deste projeto faz diferente:

1. Simula A, B e C.
2. Para cada uma, simula a resposta do humano.
3. Continua ate a profundidade escolhida.
4. Avalia os estados finais.
5. Propaga os scores de volta pela arvore.
6. Escolhe a jogada cujo pior caso ainda e o melhor para a IA.

Essa e a ideia central do Minimax.

## Custo computacional

Se cada estado tiver em media `b` jogadas possiveis e a profundidade for `d`, o Minimax puro tende a analisar aproximadamente:

```text
b^d estados
```

Exemplo:

```text
6 jogadas por estado, profundidade 4:
6^4 = 1296 estados
```

Com poda alfa-beta, muitos ramos podem ser descartados. Na pratica, isso permite aumentar a profundidade mantendo tempo aceitavel.

O projeto tambem mede o tempo da busca:

```java
long tempoInicio = System.nanoTime();
...
double tempoExecucaoMs = (tempoFim - tempoInicio) / 1_000_000.0;
```

E imprime no console:

```text
IA FINALIZOU A BUSCA EM: X ms
Profundidade maxima: Y
Melhor jogada: ...
```

## Como compilar

Sem ferramenta de build, e possivel compilar com:

```bash
javac -d /tmp/tpi-build $(find src/main -name '*.java')
```

Para executar a partir da pasta compilada:

```bash
java -cp /tmp/tpi-build main.Main
```
# Fotos do game
<img width="699" height="699" alt="image" src="https://github.com/user-attachments/assets/016cd513-66e6-4298-a419-61cb18400d13" />
<img width="699" height="699" alt="image" src="https://github.com/user-attachments/assets/e1d63267-da0c-4f9c-8114-e74f4ffb1807" />
<img width="699" height="699" alt="image" src="https://github.com/user-attachments/assets/5548bd37-6899-43cc-b003-895094bc97b3" />


