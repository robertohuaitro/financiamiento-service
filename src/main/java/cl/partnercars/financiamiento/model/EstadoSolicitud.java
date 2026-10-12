package cl.partnercars.financiamiento.model;

public enum EstadoSolicitud {
    PENDIENTE,
    EN_EVALUACION,
    APROBADA,
    RECHAZADA;

    public boolean puedeCambiarA(EstadoSolicitud nuevo) {
        return switch (this) {
            case PENDIENTE -> nuevo == EN_EVALUACION || nuevo == RECHAZADA;
            case EN_EVALUACION -> nuevo == APROBADA || nuevo == RECHAZADA;
            case APROBADA, RECHAZADA -> false;
        };
    }
}
