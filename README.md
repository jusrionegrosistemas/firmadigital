# Firma Digital con Token USB

Proyecto en Java8 que permite firmar múltiples documentos en formato PDF con un Token USB.

## Configuración del Proyecto

1. Clonar el repositorio.
2. Instalar las dependencias del proyecto Maven.

## Instalación de la aplicación

1. Maven Install, sobre el proyecto clonado, para construir "firma-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
2. Armar en la carpeta del usuario del sistema la siguiente estructura:	
```
    +---bin
    |	    firma-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    |	    OpenArgs.bat
    |	    OpenArgs.sh
    |	    +---jre
    |           ...(archivos de la JRE 1.8)
    +---FirmaConfig
    |       config.properties
    -
```
## Uso

* Windows: OpenArgs.bat <filename.sign>
* Linux: OpenArgs.sh <filename.sign>
