package proyecto_final_pp.observer;

import proyecto_final_pp.model.Envio;

public interface EnvioSubject {
    void agregarObservador(EnvioObserver observador);
    void removerObservador(EnvioObserver observador);
    void notificarObservadores();
}