# Wallager
Wallpaper Manager app for Android phones written in Java
![imagen](https://user-images.githubusercontent.com/55440044/126762396-73252e3f-bc6b-47e8-8a06-04112e02bf82.png)

El comportamiento de la aplicación es sencillo, cuando se abre por primera vez la aplicación esta genera un fichero vacío, al que se le irán añadiendo o eliminando las referencias a los diferentes álbum que cree el usuario. Tras esto la aplicación dibujará en pantalla tantos botones como haya definido en dicho fichero (en la primera vez solo 1). 
![imagen](https://user-images.githubusercontent.com/55440044/126762010-8752a2f3-e6eb-4c97-b80e-4bd84f5c77e1.png)

En el momento que el usuario presiona el botón de añadir álbum se le pedirá un nombre por escrito, con el que se definirá el nombre de un fichero, y se le permitirá elegir tantas imágenes como desee desde el Administrador de Ficheros. Al terminar de elegir las imágenes sus referencias dentro del almacenamiento (URI) son guardadas en el fichero creado para este álbum. Este fichero será leído cuando el usuario presione el botón correspondiente de la interfaz, el definido con el nombre que ha elegido y la lectura será utilizada para definir las imágenes en la siguiente pantalla. 

![imagen](https://user-images.githubusercontent.com/55440044/126762104-ea05baca-765b-4dd6-bdb4-c18133c33983.png)

Y finalmente los componentes de cambio de idioma de “fragmentsettings” alterarán la configuración local de la aplicación, haciéndola cambiar de idioma y se asegurarán que en futuros lanzamientos de la misma el idioma se mantenga cambiado, guardando esta configuración en un fichero.

![imagen](https://user-images.githubusercontent.com/55440044/126762326-c6b0f91f-0a07-46e6-ac99-63ce9bbffede.png)
