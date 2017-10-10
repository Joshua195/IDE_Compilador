package model;

public class SemanticoToken {
    private String nombreVariable;
    private String localidad;
    private String noLinea;
    private String valor;
    private String tipo;

    public SemanticoToken(String nombreVariable, String localidad, String noLinea, String valor, String tipo) {
        this.nombreVariable = nombreVariable;
        this.localidad = localidad;
        this.noLinea = noLinea;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getNombreVariable() {
        return nombreVariable;
    }

    public void setNombreVariable(String nombreVariable) {
        this.nombreVariable = nombreVariable;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getNoLinea() {
        return noLinea;
    }

    public void setNoLinea(String noLinea) {
        this.noLinea = noLinea;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "SemanticoToken{" +
                "nombreVariable='" + nombreVariable + '\'' +
                ", localidad='" + localidad + '\'' +
                ", noLinea='" + noLinea + '\'' +
                ", valor='" + valor + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
