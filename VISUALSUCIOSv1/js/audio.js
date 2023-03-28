
    var audio=document.getElementById("audio1");

    var boton = document.getElementById("boton");  
        document.getElementById("pause").addEventListener("click", function() {
            // hacemos pausa
            audio1.pause();
        }); 
 
		document.getElementById("play").addEventListener("click", function() {
			// Si deseamos que inicie siempre desde el principio
			//audioElement.currentTime = 0;
 
			// iniciamos el audio
			audio1.play();
		});
       
        alert("¡PREPARATE!. Subele el volumen a tu computadora o celular y continua...");
        alert("Deberas encontrar el boton de pausa. suerte.")

                    
    

    
  