Me he centrado en independizar los servicios, hay dos claros el "bravo" y el "cliente".
Lo que yo seguiría haciendo es:

* Crear un fichero properties para almacenar las cadenas, uno por cada clase, para mi es mas mantenible que uno para todas las clases (y mas legible).

* Independizar los "Handlers" que hay dentro de la clase "Zendesk".

* Propondría que se trabajarara en las excepciones. Desarrollar la clase "ZendeskException" y hacer que todos los "catch" las lanzaran, a la hora de mantener la aplicación y corregir errores sería muy útil.

* Propondría que tódos los métodos escribieran en el log para controlar el flujo y así saber cuando y dónde se produce la excepción. (Según mi experiencia se tarda mucho en buscar un error y sobre todo si son aplicaciones grandes que toca mucha gente como parece ser esta).

* Seguir trabajando en los test, pasar test a las clases del paquete "com.mycorp.support" no supondría mucho esfuerzo y se conseguiría una cobertura muy alta. Así como en la clase "Zendesk"
