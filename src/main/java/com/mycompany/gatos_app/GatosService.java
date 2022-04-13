/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gatos_app;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class GatosService {//esta clase tendra todas las opciones para ver los gatitocos 
    public static void verGatos() throws IOException{
        //hace toda la operacion para traer la informacion de la api
        
        //1. traemos gatos por medio de postman es decir realizamos la peticion(request) a la API con el metodo get
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.thecatapi.com/v1/images/search").method("GET", null).build();
        Response response = client.newCall(request).execute();//ejecutamos la peticion y la gaurdamos en elobjeto "response"
        
        String elJson = response.body().string();//guardamos el contenido response como tipo string, est e sun objeto Json suministrado por la Api
                                                 //se puede ver en postman
        //cortamos los corchetes
        elJson = elJson.substring(1,elJson.length());// corta el primer caracter
        elJson = elJson.substring(0, elJson.length()-1);//corta la el ultimo caracter del codigo que vamos a atraer
        
        //creamos el objeto de la clase Gson
        Gson  gson = new Gson();//enviamos el contenido de nuestro Json(id,url,etc,etc), a nustra clase Gato
        Gatos gatos = gson.fromJson(elJson, Gatos.class);//la respuesta de laa api la convierte en un objeto de tipo gato es decir la linea 29 y 30
        
        //redimensionar la imagen en caso de ser necesario
        
        Image image = null;
        try {
            URL url = new URL(gatos.getUrl());// la url del tipo gato que tenemos  la pasamos a esta variable
            image = ImageIO.read(url);// se puede cargar la imagen en esta variable
            
            ImageIcon fondoGato = new ImageIcon(image); //fondo del gato
            
            //validamos size
            
            if (fondoGato.getIconWidth() >800) {
                //redimensionamos
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800, 600, java.awt.Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
                    
            }
            
            String menu = "Opciones: \n"
                    +" 1.Ver otra imagen\n"
                    +" 2.Favorito\n"
                    +" 3.Volver \n";
            
            String[] botones = {
                "Ver otra imagen", "Favoritos", "Volver"
            };
            
            String id_gato = gatos.getId();
            String opcion = (String)JOptionPane.showInputDialog(null,menu,id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones,botones[0]);
            
            int seleccion = -1;
            //validamos opcion
            for(int i=0; i<botones.length;i++){
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }
            
            switch(seleccion){
                case 0:
                    verGatos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public static void favoritoGato(Gatos gato){
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n    \"image_id\": \""+gato.getId()+"\"\r\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", gato.getApikey())
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static  void verFavoritos(String apikey) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .method("GET", null)
                .addHeader("x-api-key", apikey)
                .build();
        Response response = client.newCall(request).execute();
        //guardamos el string con la repsuesta 
        String elJson = response.body().string();
        
        //Creamos el obejto Gson 
        Gson gson = new Gson();
        GatosFav[] gatosArray = gson.fromJson(elJson,GatosFav[].class);
        
        if (gatosArray.length > 0) {
            int min =1;
            int max = gatosArray.length;
            int aleatorio = (int)(Math.random()*((max-min)+1) + min);
            int indice = aleatorio - 1;
            
            GatosFav gatofav = gatosArray[indice];
            
            //redimencioinamos
            Image image = null;
            try {
                URL url = new URL(gatofav.image.getUrl());// la url del tipo gato que tenemos  la pasamos a esta variable
                image = ImageIO.read(url);// se puede cargar la imagen en esta variable

                ImageIcon fondoGato = new ImageIcon(image); //fondo del gato

                //validamos size
                if (fondoGato.getIconWidth() > 800) {
                    //redimensionamos
                    Image fondo = fondoGato.getImage();
                    Image modificada = fondo.getScaledInstance(800, 600, java.awt.Image.SCALE_SMOOTH);
                    fondoGato = new ImageIcon(modificada);

                }

                String menu = "Opciones: \n"
                        + " 1.Ver otra imagen\n"
                        + " 2.Eliminar Favorito\n"
                        + " 3.Volver \n";

                String[] botones = {
                    "Ver otra imagen", "Eliminar Favorito", "Volver"
                };

                String id_gato = gatofav.getId();
                String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);

                int seleccion = -1;
                //validamos opcion
                for (int i = 0; i < botones.length; i++) {
                    if (opcion.equals(botones[i])) {
                        seleccion = i;
                    }
                }

                switch (seleccion) {
                    case 0:
                        verFavoritos(apikey);
                        break;
                    case 1:
                        borrarFavorito(gatofav);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        
    }
    
    public static void borrarFavorito(GatosFav gatofav){
        
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/"+gatofav.getId()+"")
                    .method("DELETE", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", "1f7f9387-da86-4073-99fe-d2b1ebe443e4")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
