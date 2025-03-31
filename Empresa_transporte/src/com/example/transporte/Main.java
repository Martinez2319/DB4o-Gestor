package com.example.transporte;

import com.db4o.*;
import com.db4o.query.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Main {
    private static ObjectContainer db;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = 0;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                ejecutarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        } while (opcion != 15);

        if (db != null) {
            db.close();
        }
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n=== Sistema de Gestión de Transportes ===");
        System.out.println("1. Crear base de datos");
        System.out.println("2. Listar bases de datos");
        System.out.println("3. Iniciar base de datos");
        System.out.println("4. Modificar base de datos");
        System.out.println("5. Borrar base de datos");
        System.out.println("6. Crear nueva colección");
        System.out.println("7. Insertar nuevo documento");
        System.out.println("8. Consultar documentos");
        System.out.println("9. Actualizar documento");
        System.out.println("10. Eliminar documento");
        System.out.println("11. Listar colecciones");
        System.out.println("12. Modificar colección");
        System.out.println("13. Borrar colección");
        System.out.println("14. Borrar registro específico");
        System.out.println("15. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1: crearBaseDatos(); break;
            case 2: listarBasesDatos(); break;
            case 3: iniciarBaseDatos(); break;
            case 4: modificarBaseDatos(); break;
            case 5: borrarBaseDatos(); break;
            case 6: crearColeccion(); break;
            case 7: insertarDocumento(); break;
            case 8: consultarDocumentos(); break;
            case 9: actualizarDocumento(); break;
            case 10: eliminarDocumento(); break;
            case 11: listarColecciones(); break;
            case 12: modificarColeccion(); break;
            case 13: borrarColeccion(); break;
            case 14: borrarRegistro(); break;
            case 15: System.out.println("Saliendo del sistema..."); break;
            default: System.out.println("Opción no válida");
        }
    }

    private static void crearBaseDatos() {
        System.out.print("Nombre para la nueva base de datos (ejemplo: transportes.db4o): ");
        String nombreDB = scanner.nextLine();
        try {
            ObjectContainer tempDB = Db4oEmbedded.openFile(nombreDB);
            tempDB.close();
            System.out.println("Base de datos creada exitosamente: " + nombreDB);
        } catch (Exception e) {
            System.out.println("Error al crear la base de datos: " + e.getMessage());
        }
    }

    private static void listarBasesDatos() {
        File directorio = new File(".");
        File[] archivosDB = directorio.listFiles((dir, name) -> name.endsWith(".db4o"));

        if (archivosDB != null && archivosDB.length > 0) {
            System.out.println("\nBases de datos encontradas:");
            for (File archivo : archivosDB) {
                System.out.println("- " + archivo.getName());
            }
        } else {
            System.out.println("No se encontraron bases de datos.");
        }
    }

    private static void iniciarBaseDatos() {
        System.out.print("Nombre de la base de datos a abrir: ");
        String nombreDB = scanner.nextLine();
        try {
            if (db != null) {
                db.close();
            }
            db = Db4oEmbedded.openFile(nombreDB);
            System.out.println("Base de datos iniciada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al abrir la base de datos: " + e.getMessage());
        }
    }

    private static void modificarBaseDatos() {
        System.out.print("Nombre actual de la base de datos: ");
        String nombreActual = scanner.nextLine();
        System.out.print("Nuevo nombre: ");
        String nombreNuevo = scanner.nextLine();

        File archivoActual = new File(nombreActual);
        File archivoNuevo = new File(nombreNuevo);

        if (archivoActual.renameTo(archivoNuevo)) {
            System.out.println("Base de datos renombrada exitosamente.");
        } else {
            System.out.println("Error al renombrar la base de datos.");
        }
    }

    private static void borrarBaseDatos() {
        System.out.print("Nombre de la base de datos a eliminar: ");
        String nombreDB = scanner.nextLine();
        File archivo = new File(nombreDB);

        if (archivo.delete()) {
            System.out.println("Base de datos eliminada exitosamente.");
        } else {
            System.out.println("Error al eliminar la base de datos.");
        }
    }

    private static void crearColeccion() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.println("\nCreación de nueva colección");
        System.out.print("Nombre de la colección: ");
        String nombreColeccion = scanner.nextLine();

        Map<String, String> esquema = new HashMap<>();
        boolean continuarAgregando = true;

        while (continuarAgregando) {
            System.out.print("Nombre del campo: ");
            String nombreCampo = scanner.nextLine();

            System.out.println("Tipo de dato:");
            System.out.println("1. Texto");
            System.out.println("2. Número");
            System.out.println("3. Fecha");
            System.out.println("4. Booleano");
            System.out.print("Seleccione el tipo (1-4): ");

            String tipoCampo = scanner.nextLine();
            esquema.put(nombreCampo, tipoCampo);

            System.out.print("¿Desea agregar otro campo? (s/n): ");
            continuarAgregando = scanner.nextLine().equalsIgnoreCase("s");
        }

        DocumentoEsquema docEsquema = new DocumentoEsquema(nombreColeccion, esquema);
        db.store(docEsquema);
        db.commit();
        System.out.println("Colección creada exitosamente.");
    }

    private static void listarColecciones() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        ObjectSet<DocumentoEsquema> esquemas = db.query(DocumentoEsquema.class);
        if (esquemas.isEmpty()) {
            System.out.println("No hay colecciones creadas en la base de datos.");
        } else {
            System.out.println("\nColecciones disponibles:");
            for (DocumentoEsquema esquema : esquemas) {
                System.out.println("- " + esquema.getNombreColeccion());
            }
        }
    }

    private static void modificarColeccion() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.print("Nombre de la colección a modificar: ");
        String nombreColeccion = scanner.nextLine();

        ObjectSet<DocumentoEsquema> esquemas = db.query(new Predicate<DocumentoEsquema>() {
            @Override
            public boolean match(DocumentoEsquema esquema) {
                return esquema.getNombreColeccion().equals(nombreColeccion);
            }
        });

        if (esquemas.isEmpty()) {
            System.out.println("La colección no existe.");
            return;
        }

        DocumentoEsquema esquema = esquemas.get(0);

        System.out.println("Esquema actual de la colección:");
        for (Map.Entry<String, String> campo : esquema.getEsquema().entrySet()) {
            System.out.println("- " + campo.getKey() + " (" + campo.getValue() + ")");
        }

        System.out.println("\nOpciones:");
        System.out.println("1. Agregar un nuevo campo");
        System.out.println("2. Eliminar un campo existente");
        System.out.print("Seleccione una opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());

        if (opcion == 1) {
            System.out.print("Nombre del nuevo campo: ");
            String nuevoCampo = scanner.nextLine();
            System.out.println("Tipo de dato:");
            System.out.println("1. Texto");
            System.out.println("2. Número");
            System.out.println("3. Fecha");
            System.out.println("4. Booleano");
            System.out.print("Seleccione el tipo (1-4): ");
            String tipoCampo = scanner.nextLine();

            esquema.getEsquema().put(nuevoCampo, tipoCampo);

            // Actualizar todos los documentos existentes con el nuevo campo como null
            ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
                @Override
                public boolean match(Documento doc) {
                    return doc.getNombreColeccion().equals(nombreColeccion);
                }
            });

            for (Documento doc : documentos) {
                doc.getValores().put(nuevoCampo, null);
                db.store(doc);
            }

            System.out.println("Campo agregado exitosamente y documentos actualizados con valor null para el nuevo campo.");
        } else if (opcion == 2) {
            System.out.print("Nombre del campo a eliminar: ");
            String campoEliminar = scanner.nextLine();

            if (esquema.getEsquema().containsKey(campoEliminar)) {
                esquema.getEsquema().remove(campoEliminar);
                
                // Eliminar el campo de todos los documentos existentes
                ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
                    @Override
                    public boolean match(Documento doc) {
                        return doc.getNombreColeccion().equals(nombreColeccion);
                    }
                });

                for (Documento doc : documentos) {
                    doc.getValores().remove(campoEliminar);
                    db.store(doc);
                }
                
                System.out.println("Campo eliminado exitosamente de la colección y todos los documentos.");
            } else {
                System.out.println("El campo no existe en el esquema.");
            }
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        db.store(esquema);
        db.commit();
        System.out.println("Esquema de la colección modificado exitosamente.");
    }

    private static void borrarColeccion() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.print("Nombre de la colección a eliminar: ");
        String nombreColeccion = scanner.nextLine();

        ObjectSet<DocumentoEsquema> esquemas = db.query(new Predicate<DocumentoEsquema>() {
            @Override
            public boolean match(DocumentoEsquema esquema) {
                return esquema.getNombreColeccion().equals(nombreColeccion);
            }
        });

        if (esquemas.isEmpty()) {
            System.out.println("La colección no existe.");
            return;
        }

        for (DocumentoEsquema esquema : esquemas) {
            db.delete(esquema);
        }

        ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
            @Override
            public boolean match(Documento doc) {
                return doc.getNombreColeccion().equals(nombreColeccion);
            }
        });

        for (Documento doc : documentos) {
            db.delete(doc);
        }

        db.commit();
        System.out.println("Colección eliminada exitosamente.");
    }

    private static void insertarDocumento() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        ObjectSet<DocumentoEsquema> esquemas = db.query(DocumentoEsquema.class);
        if (esquemas.size() == 0) {
            System.out.println("No hay colecciones creadas. Cree una primero.");
            return;
        }

        System.out.println("\nColecciones disponibles:");
        for (DocumentoEsquema esquema : esquemas) {
            System.out.println("- " + esquema.getNombreColeccion());
        }

        System.out.print("\nSeleccione la colección: ");
        String nombreColeccion = scanner.nextLine();

        DocumentoEsquema esquemaSeleccionado = null;
        for (DocumentoEsquema esquema : esquemas) {
            if (esquema.getNombreColeccion().equals(nombreColeccion)) {
                esquemaSeleccionado = esquema;
                break;
            }
        }

        if (esquemaSeleccionado == null) {
            System.out.println("Colección no encontrada.");
            return;
        }

        Map<String, Object> valores = new HashMap<>();
        for (Map.Entry<String, String> campo : esquemaSeleccionado.getEsquema().entrySet()) {
            System.out.print(campo.getKey() + ": ");
            String valor = scanner.nextLine();
            valores.put(campo.getKey(), valor);
        }

        Documento documento = new Documento(nombreColeccion, valores);
        db.store(documento);
        db.commit();
        System.out.println("Documento insertado exitosamente.");
    }

    private static void consultarDocumentos() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.print("Nombre de la colección: ");
        String nombreColeccion = scanner.nextLine();

        ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
            @Override
            public boolean match(Documento doc) {
                return doc.getNombreColeccion().equals(nombreColeccion);
            }
        });

        mostrarDocumentos(documentos);
    }
    private static void borrarRegistro() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        // Mostrar colecciones disponibles
        ObjectSet<DocumentoEsquema> esquemas = db.query(DocumentoEsquema.class);
        if (esquemas.isEmpty()) {
            System.out.println("No hay colecciones disponibles.");
            return;
        }

        System.out.println("\nColecciones disponibles:");
        List<String> nombreColecciones = new ArrayList<>();
        int index = 1;
        for (DocumentoEsquema esquema : esquemas) {
            System.out.println(index + ". " + esquema.getNombreColeccion());
            nombreColecciones.add(esquema.getNombreColeccion());
            index++;
        }

        // Seleccionar colección
        System.out.print("\nSeleccione el número de la colección (1-" + nombreColecciones.size() + "): ");
        try {
            int seleccionColeccion = Integer.parseInt(scanner.nextLine());
            if (seleccionColeccion < 1 || seleccionColeccion > nombreColecciones.size()) {
                System.out.println("Número de colección inválido.");
                return;
            }

            String nombreColeccion = nombreColecciones.get(seleccionColeccion - 1);

            // Obtener y mostrar documentos de la colección
            ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
                @Override
                public boolean match(Documento doc) {
                    return doc.getNombreColeccion().equals(nombreColeccion);
                }
            });

            if (documentos.isEmpty()) {
                System.out.println("No hay documentos en esta colección.");
                return;
            }

            System.out.println("\nDocumentos en la colección '" + nombreColeccion + "':");
            List<Documento> listaDocumentos = new ArrayList<>();
            index = 1;
            for (Documento doc : documentos) {
                System.out.println("\nDocumento " + index + ":");
                for (Map.Entry<String, Object> campo : doc.getValores().entrySet()) {
                    System.out.println("  " + campo.getKey() + ": " + campo.getValue());
                }
                listaDocumentos.add(doc);
                index++;
            }

            // Seleccionar documento a eliminar
            System.out.print("\nSeleccione el número del documento a eliminar (1-" + listaDocumentos.size() + "): ");
            int seleccionDocumento = Integer.parseInt(scanner.nextLine());
            
            if (seleccionDocumento >= 1 && seleccionDocumento <= listaDocumentos.size()) {
                Documento docAEliminar = listaDocumentos.get(seleccionDocumento - 1);
                
                // Confirmar eliminación
                System.out.print("¿Está seguro de eliminar este documento? (s/n): ");
                String confirmacion = scanner.nextLine();
                
                if (confirmacion.equalsIgnoreCase("s")) {
                    db.delete(docAEliminar);
                    db.commit();
                    System.out.println("Documento eliminado exitosamente.");
                } else {
                    System.out.println("Operación cancelada.");
                }
            } else {
                System.out.println("Número de documento inválido.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        }
    }

    private static void actualizarDocumento() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.print("Nombre de la colección: ");
        String nombreColeccion = scanner.nextLine();

        System.out.print("Campo a actualizar: ");
        String campo = scanner.nextLine();

        System.out.print("Valor actual: ");
        String valorActual = scanner.nextLine();

        System.out.print("Nuevo valor: ");
        String nuevoValor = scanner.nextLine();

        ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
            @Override
            public boolean match(Documento doc) {
                return doc.getNombreColeccion().equals(nombreColeccion) &&
                       doc.getValores().containsKey(campo) &&
                       doc.getValores().get(campo).equals(valorActual);
            }
        });

        for (Documento doc : documentos) {
            doc.getValores().put(campo, nuevoValor);
            db.store(doc);
        }
        db.commit();
        System.out.println("Documentos actualizados.");
    }

    private static void eliminarDocumento() {
        if (db == null) {
            System.out.println("Error: Primero debe iniciar una base de datos.");
            return;
        }

        System.out.print("Nombre de la colección: ");
        String nombreColeccion = scanner.nextLine();

        System.out.print("Campo de búsqueda: ");
        String campo = scanner.nextLine();

        System.out.print("Valor a buscar: ");
        String valor = scanner.nextLine();

        ObjectSet<Documento> documentos = db.query(new Predicate<Documento>() {
            @Override
            public boolean match(Documento doc) {
                return doc.getNombreColeccion().equals(nombreColeccion) &&
                       doc.getValores().containsKey(campo) &&
                       doc.getValores().get(campo).equals(valor);
            }
        });

        for (Documento doc : documentos) {
            db.delete(doc);
        }
        db.commit();
        System.out.println("Documentos eliminados.");
    }

    private static void mostrarDocumentos(ObjectSet<Documento> documentos) {
        if (documentos.isEmpty()) {
            System.out.println("No se encontraron documentos.");
        } else {
            for (Documento doc : documentos) {
                System.out.println("Colección: " + doc.getNombreColeccion());
                System.out.println("Valores: " + doc.getValores());
            }
        }
    }
}

class DocumentoEsquema implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreColeccion;
    private Map<String, String> esquema;

    public DocumentoEsquema(String nombreColeccion, Map<String, String> esquema) {
        this.nombreColeccion = nombreColeccion;
        this.esquema = esquema;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }

    public Map<String, String> getEsquema() {
        return esquema;
    }
}

class Documento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreColeccion;
    private Map<String, Object> valores;

    public Documento(String nombreColeccion, Map<String, Object> valores) {
        this.nombreColeccion = nombreColeccion;
        this.valores = valores;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }

    public Map<String, Object> getValores() {
        return valores;
    }
}