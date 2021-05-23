package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class jogo extends ApplicationAdapter {

    //Construção
    private SpriteBatch batch;    //metodo interno que associa as informações que vão ser renderizadas na tela
    private Texture[] passaros;   //imagens do passaro
    private Texture fundo;        //imagem do background
    private Texture canoTopo;     //imagem do cano de cima
    private Texture canoBaixo;    //imagem do cano baixo
    private int pontos = 0;       //pontuação do jogador
    BitmapFont textoPontuacao;    //texto pontuação

    //Movimentação
    private float variacao = 0;                       //variação da altura
    private int gravidade = 0;                        //adiciona o valor da gravidade ao passaro
    private float posicaoInicialVerticalPassaro = 0;  //posiçao inicial
    private float posicaoCanoHorizontal = 0;          //posição do cano na horizontal
    private float posicaoCanoVertical;                //posição do cano na vertical
    private float espaçoEntreCanos;                   //distancia entre os canos
    private boolean passouCano = false;               //se passou ou nao pelo cano
    private Random random;                            //torna aleatorio o espaço entre os canos

    //ajusta a tela de acordo com o celular
    private float larguraDispositivo;
    private float alturaDispositivo;

    //Colisão
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;              //collider do passaro
    private Rectangle retanguloCanoCima;        //collider cano cima
    private Rectangle retanguloCanoBaixo;       //collider cano baixo


    @Override
    public void create() {


        inicializaTexturas();
        inicializaObjetos();

        //monta a tela, instanciando os objetos
    }

    @Override
    public void render() {

        verificarEstadoJogo();
        desenharTexturas();
        validarPontos();
        detectarColisão();

        //parte de layout  e aplica as informações
    }




    @Override
    public void dispose() {

        //entrega a aplicação, retornando os dados
    }

    private void inicializaTexturas() {

        passaros = new Texture[3];                             //instancia a imagem para a interface
        //pega as imagens do passaro
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");          //instancia o fundo

        //pega as imagens dos canos
        canoTopo = new Texture("cano_topo_maior.png");
        canoBaixo = new Texture("cano_baixo_maior.png");

    }

    private void inicializaObjetos() {

        batch = new SpriteBatch();
        random = new Random();                                 //randomiza os canos

        larguraDispositivo = Gdx.graphics.getWidth();           //largura do dispositivo.
        alturaDispositivo = Gdx.graphics.getHeight();           //altura do dispositivo.
        posicaoInicialVerticalPassaro = alturaDispositivo / 2;  //coloca o passaro no meio da tela
        posicaoCanoHorizontal = larguraDispositivo;             //ajusta os danos na tela
        espaçoEntreCanos = 350;                                 //distancia entre os canos na tela

        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);                   //cor do texto
        textoPontuacao.getData().setScale(10);                  //Tamanho da fonte

    }

    private void verificarEstadoJogo() {

       posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;       //velocidade dos canos em direção ao player

       if(posicaoCanoHorizontal < - canoBaixo.getWidth()){               //checa a largura da tela para avançar assim que a tela acabar
           posicaoCanoHorizontal = larguraDispositivo;
           posicaoCanoVertical = random.nextInt(400) -200;            //torna os espaçamentos aleatorios
           passouCano = false;                                           //volta o passoucano para false
       }

        boolean toqueTela = Gdx.input.justTouched();            //verifica se o jogador tocou na tela

        if (Gdx.input.justTouched()) {                          //impulsiona o passaro para cima
            gravidade = -25;
        }

        if (posicaoInicialVerticalPassaro > 0 || toqueTela)                               //associa o toque na tela com a gravidade.
            posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 3)                                         //muda a animação de acordo com a variação
            variacao = 0;

        gravidade++;
    }

    private void desenharTexturas() {

        batch.begin();   //inicializa

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);          //coloca o background de acordo com a tamanho da tela
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro);    //instacia o passaro ja com animação na tela

        //instancia os canos e suas distancias
        batch.draw(canoBaixo, posicaoCanoHorizontal -100, alturaDispositivo/2 - canoBaixo.getHeight() - espaçoEntreCanos/2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal -100,alturaDispositivo/2 + espaçoEntreCanos + posicaoCanoVertical);

        textoPontuacao.draw(batch,String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 100);

        batch.end();   //termino

    }
    private void validarPontos() {

        if(posicaoCanoHorizontal < 50 - passaros[0].getWidth()){
           if(!passouCano){                                                       // verifica se o jogador passou do cano
               pontos++;                                                          //aumenta a pontuação
               passouCano = true;                                                 //se torna verdadeiro ao passar do cano
           }

        }

    }
    private void detectarColisão() {

        circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);                                     //associa collider ao passaro.
        retanguloCanoCima.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoTopo.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight() );        //associacollider ao cano de cima
        retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());     //associa ollider ao cano de baixo

        boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);           //checa colisão
        boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        if(colisaoCanoBaixo || colisaoCanoCima){                      //mensagem se bater no cano
            Gdx.app.log("log", "bateu");
        }
    }
}
