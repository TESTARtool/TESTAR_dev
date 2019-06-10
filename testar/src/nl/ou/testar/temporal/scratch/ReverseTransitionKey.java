package nl.ou.testar.temporal.scratch;

public class ReverseTransitionKey {

    private final String srcState;
    private final String decodedAPConjunct;
    //decoded conjunction of all boolean Ap's
    // that make up the transition in the model ( ~ edge)
    //decoded means that Ap's are prefixed ith 'ap". e.g ap5, ap13



    public ReverseTransitionKey(String srcState, String decodedAPConjunct) {
        this.srcState = srcState;
        this.decodedAPConjunct = decodedAPConjunct;
    }

    @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReverseTransitionKey)) return false;
            ReverseTransitionKey key = (ReverseTransitionKey) o;
            return srcState == key.srcState &&  decodedAPConjunct ==key.decodedAPConjunct;
        }
        @Override
        public int hashCode() {

            int result = srcState.hashCode();
            result = 31 * result + decodedAPConjunct.hashCode();
            return result;
        }


}
