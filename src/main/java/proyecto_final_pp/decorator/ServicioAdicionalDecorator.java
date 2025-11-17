package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public abstract class ServicioAdicionalDecorator implements EnvioConServicioAdicional {

    protected EnvioConServicioAdicional envioDecorado;

    public ServicioAdicionalDecorator(EnvioConServicioAdicional envioDecorado) {
        if (envioDecorado == null) {
            throw new IllegalArgumentException("El envío a decorar no puede ser nulo");
        }
        this.envioDecorado = envioDecorado;
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        return envioDecorado.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
    }


    public abstract String getDescripcionServicio();
    public abstract double getCostoAdicional();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " decorando: " + envioDecorado.toString();
    }

    public String getCadenaDecoradores() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDescripcionServicio());

        if (envioDecorado instanceof ServicioAdicionalDecorator) {
            sb.append(" -> ").append(((ServicioAdicionalDecorator) envioDecorado).getCadenaDecoradores());
        } else if (envioDecorado instanceof EnvioBase) {
            sb.append(" -> EnvioBase");
        }

        return sb.toString();
    }

    public int getTotalDecoradores() {
        int count = 1;
        if (envioDecorado instanceof ServicioAdicionalDecorator) {
            count += ((ServicioAdicionalDecorator) envioDecorado).getTotalDecoradores();
        }
        return count;
    }

    public boolean tieneServicioAplicado(Class<?> tipoServicio) {
        if (tipoServicio.isInstance(this)) {
            return true;
        }
        if (envioDecorado instanceof ServicioAdicionalDecorator) {
            return ((ServicioAdicionalDecorator) envioDecorado).tieneServicioAplicado(tipoServicio);
        }
        return false;
    }

    public EnvioConServicioAdicional getEnvioDecorado() {
        return envioDecorado;
    }

    public void setEnvioDecorado(EnvioConServicioAdicional envioDecorado) {
        if (envioDecorado == null) {
            throw new IllegalArgumentException("El envío a decorar no puede ser nulo");
        }
        this.envioDecorado = envioDecorado;
    }
}