package org.daisy.emerson.ui;

import org.daisy.reader.history.HistoryList;
import org.daisy.reader.notes.Notes;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return DefaultPerspective.PERSPECTIVE_ID;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);		
	}
	@Override
	public boolean preShutdown() {
		HistoryList.getInstance().dispose();
		return super.preShutdown();
	}
	@Override
	public IStatus restoreState(IMemento memento) {
		try{
			//System.err.println("creating historylist");
			HistoryList.create(memento);
			Notes.create(memento);
		}catch (Throwable t) {
			Activator.getDefault().logError(t.getLocalizedMessage(), t);
		}	
		return super.restoreState(memento);
	}
	
	@Override
	public IStatus saveState(IMemento memento) {
		try{
			//System.err.println("saving historylist");
			HistoryList.getInstance().serialize(memento);			
			Notes.getInstance().serialize(memento);
		}catch (Throwable t) {
			Activator.getDefault().logError(t.getLocalizedMessage(), t);
		}
		return super.saveState(memento);
	}

}
