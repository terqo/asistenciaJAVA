confirm("Hola caramelito")
var edad=prompt("¿Que edad tienes?")
if (edad >=15 && edad <=22)
alert("Bienvenido sh4b@")
if(edad<15)
alert("¡ALERTA!. niño rata")
if(edad>22)
alert("¿k3 H4Se3s Aki Mm0oXx0o?")

confirm("TOMALE SS A LOS PAGINA DE COLORES. COLOR QUE SALGA, COLOR FAVORITO DE TU CRUSH?")
confirm("solo preguntandole a tu crush sabras si es o no es su color. JUST SAYING!")
setInterval(function(){
    var x = Math.round( Math.random() * 255 );
    var y = Math.round( Math.random() * 255 );
    var z = Math.round( Math.random() * 255 );
    var bg = "background:rgb("+x+", "+y+", "+z+");";
    var element = document.getElementById("random-background");
    element.style = bg;
}, 1000);