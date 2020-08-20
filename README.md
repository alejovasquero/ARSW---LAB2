# Arquitecturas de Software
# Laboratorio 2 Paralelismo y Concurrencia

## Integrantes
- David Alejandro Vasquez Carreño
- Michael Jefferson Ballesteros Coca

____________

#### Part I - Before finishing class

1. Chequeo de consumo de CPU en ejecución. 
    
    ![](img/usoAlto.png)
    
    Este consumo es debido a que el consumidor hace una espera activa para verificar
    que la cola tenga elementos de los que sacar productos. Como regla, los threads o procesos 
    que esperen a que un recurso se libere no deben hacer uso 
    de cómputo. 
    
      ```
    while (true) {
                if (queue.size() > 0) {
                    int elem=queue.poll();
                    System.out.println("Consumer consumes "+elem);                                
                }
            }
      ```

2. El consumo de CPU se va a mejorar a través del uso de wait() y notify() sobre la cola, de
modo de que el consumidor no consuma recursos mientras la cola está vacía.

    ```
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
      
   ```
    synchronized(queue){
        queue.notify();
    }
   ```
Vemos que el consumo de CPU de ha reducido.

   ![](img/usoBajo.png)


## Construido con

* [Maven](https://maven.apache.org/) - Manejo de dependencias


## Contribuciones

* **Alejandro Vasquez** - *Extender* - [alejovasquero](https://github.com/alejovasquero)
* **Michael Ballesteros** - *Extender* - [Wasawsky](https://github.com/Wasawsky)