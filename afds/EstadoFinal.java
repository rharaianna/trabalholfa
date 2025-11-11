package afds;

public class EstadoFinal extends Estado {
    private String significado;

    public EstadoFinal(String valor, String significado) {
        super(valor);
        this.significado = significado;
    }

    public String getSignificado() {
        return significado;
    }

    public void setSignificado(String significado) {
        this.significado = significado;
    }
}
