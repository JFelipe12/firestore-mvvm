# firestore-mvvm-clean

Capa de datos : Contiene implementaciones de repositorios y fuentes de datos, la definición de base de datos y sus DAO, 
las definiciones de API de red, algunos mapeadores para convertir modelos de API de red en modelos de base de datos y viceversa.

Capa de presentación: No existe ningún tipo de lógica de negocio y está completamente desvinculado de la capa de datos. Contiene Activitys, Fragments,ViewModels y Adaptadores.

Capa de dominio : Sirve como mediador entre el viewmodel y el repositorio, para este caso contiene los casos de uso, sin embargo usaremos flujos,corutinas 
y livedatas para tratar de agilizar la implementación
