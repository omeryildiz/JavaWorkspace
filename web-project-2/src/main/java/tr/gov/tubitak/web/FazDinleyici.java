package tr.gov.tubitak.web;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class FazDinleyici implements PhaseListener{

	@Override
	public void afterPhase(PhaseEvent event) {
		System.out.println("faz : " + event.getPhaseId());
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
