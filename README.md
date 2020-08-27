# Arquitecturas de Software
# Laboratorio 2 Paralelismo y Concurrencia

## Integrantes
- David Alejandro Vasquez Carreño
- Michael Jefferson Ballesteros Coca

____________
#### Instalación

Vamos a compilar el código con maven.
   ```console
mvn compile
   ```


Para ejecutar la aplicación.

  ```console
mvn exec:java -Dexec.mainClass="edu.eci.arsw.highlandersim.ControlFrame"
   ```

Para ejecutar las pruebas

  ```console
mvn test
   ```
____________

#### Part I - Before finishing class

1. Chequeo de consumo de CPU en ejecución. 
    
    ![](img/usoAlto.png)
    
    Este consumo es debido a que el consumidor hace una espera activa para verificar
    que la cola tenga elementos de los que sacar productos. Como regla, los threads o procesos 
    que esperen a que un recurso se libere no deben hacer uso 
    de cómputo. 
    
      ```java
    while (true) {
                if (queue.size() > 0) {
                    int elem=queue.poll();
                    System.out.println("Consumer consumes "+elem);                                
                }
            }
      ```

2. El consumo de CPU se va a mejorar a través del uso de wait() y notify() sobre la cola, de
modo de que el consumidor no consuma recursos mientras la cola está vacía.

    ```java
    synchronized(queue){
        if (queue.size() > 0) {
            int elem=queue.poll();
            System.out.println("Consumer consumes "+elem);                                
        }else{
            try {
                queue.wait();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }   
    ```

        
El bloque de sincronización pide el lock sobre el objeto y hace que el hilo consumidor espere si no
hay elementos sobre la cola.

Del lado del productor, cada vez que insertemos nuevos elementos el la cola, notificamos
a los hilos que estén esperando por este recurso.
      
   ```java
    synchronized(queue){
        queue.notify();
    }
   ```
Vemos que el consumo de CPU de ha reducido.

   ![](img/usoBajo.png)

3. Consumidor lento y productor rápido

La implementación es muy parecida a la del punto 2, pero el que va a esperar es el productor. 
El productor espera a que haya espacio.  

***PRODUCTOR***

  ```java
  if(stockLimit>queue.size()){
      dataSeed = dataSeed + rand.nextInt(100);
      System.out.println("Producer added " + dataSeed);
      queue.add(dataSeed);
  }else{
      synchronized(queue){
            try {
                queue.wait();
            } catch (Exception e) {
                //TODO: handle exception
            }
      }
  }

  ```


***CONSUMIDOR***

  ```java
  synchronized(queue){
      if (queue.size() > 0) {
          int elem=queue.poll();
          System.out.println("Consumer consumes "+elem); 
          queue.notify();                              
      }
  }
  ```

![](img/usoBajoP2.png)
Vemos que el consumo de CPU se ha mantenido bajo.


____________


#### Part II - JAVA IMMORTALS

RESPUESTAS

1. Para N jugadores, ¿Cuál debería ser la suma de las vidas de los jugadores según el invariante?

De manera general, siendo N los jugadores y ***health*** la vida.

Vida total: ***N*** x ***health***

De manera especifica: N x 100

2.Verificación del invariante

    __PAUSA 1__
    
    ![](img/IMMORTALS/invarianteMal1.PNG)   
    
    __PAUSA 2__
        
    ![](img/IMMORTALS/invarianteMal2.PNG)  
    
    __PAUSA 3__
            
    ![](img/IMMORTALS/invarianteMal3.PNG)  
    
Vemos que el invariante no se cumple :anguished:


3.Invariante luego de implementar la pausa ***COMPLETA*** de threads.
 
    __PAUSA 1__
    
    ![](img/IMMORTALS/invarianteRound2-1.PNG)   
    
    __PAUSA 2__
        
    ![](img/IMMORTALS/invarianteRound2-2.PNG)  
    
    __PAUSA 3__
            
    ![](img/IMMORTALS/invarianteRound2-3.PNG)  
    
Vemos que el invariante no se cumple :neutral_face: aún

4.Identificación y solución de regiones críticas

Una región crítica que debe ser protegida con exclusión mutua es la pelea entre inmortales en el método ***fight***.

  ```java
if (i2.getHealth() > 0) {
    i2.changeHealth(i2.getHealth() - defaultDamageValue);
    this.health += defaultDamageValue;
    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
} else {
    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
}
  ```

Vamos a hacer la solución a través de un bloque sincronizado anidado.

5.Deadlock de inmortales

Vemos gracias a jstack, que ha habido un bloque mutuo entre threads.
  
  ```console
jstack -l 16008
  ```
Dos threads están esperando por un monitor que ya poseen, por lo que se quedan esperando por siempre.

  ```
"im0":
        at edu.eci.arsw.highlandersim.Immortal.fight(Immortal.java:80)
        - waiting to lock <0x00000000c4fa06f8> (a edu.eci.arsw.highlandersim.Immortal)
        - locked <0x00000000c4f9d180> (a edu.eci.arsw.highlandersim.Immortal)
        at edu.eci.arsw.highlandersim.Immortal.run(Immortal.java:68)
"im1":
        at edu.eci.arsw.highlandersim.Immortal.fight(Immortal.java:80)
        - waiting to lock <0x00000000c4f9d180> (a edu.eci.arsw.highlandersim.Immortal)
        - locked <0x00000000c4fa06f8> (a edu.eci.arsw.highlandersim.Immortal)
        at edu.eci.arsw.highlandersim.Immortal.run(Immortal.java:68)
  ```

6.Implementación con lista concurrente

Al eliminar elementos de manera concurrente, sube la posibilidad de acceder a indices vacíos del arreglo.

   ![](img/IMMORTALS/PROBLEMALIST.PNG) 
   
Entre más inmortales haya, más accesos aleatorios se harán en la lista, pero al mismo tiempo se eliminan elementos.
Se debe controlar los accesos a la lista con excepciones si no se quiere hacer sincronización.
____________
## Construido con

* [Maven](https://maven.apache.org/) - Manejo de dependencias


## Contribuciones

* **Alejandro Vasquez** - *Extender* - [alejovasquero](https://github.com/alejovasquero)
* **Michael Ballesteros** - *Extender* - [Wasawsky](https://github.com/Wasawsky)