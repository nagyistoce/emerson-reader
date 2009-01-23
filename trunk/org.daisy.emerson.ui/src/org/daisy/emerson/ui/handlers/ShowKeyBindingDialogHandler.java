package org.daisy.emerson.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class ShowKeyBindingDialogHandler extends AbstractHandler {
//	private static final String COMMAND_PREFIX = "org.daisy.emerson";
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
    	IBindingService bindingService;
    	bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
    	
//    	ICommandService commandService;
//    	commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
//    	List<String> commands = new ArrayList<String>(); 
//    	commands.addAll(commandService.getDefinedCommandIds());
//    	Collections.sort(commands);
//    	Iterator<?> iterator = commands.iterator();
//    	int i = 0;
//    	while(iterator.hasNext()) {
//    		String cID = (String) iterator.next();
//    		
//    		if(cID.startsWith(COMMAND_PREFIX)) {
//    			i++;
//    			System.err.print("Command: " + cID + " :: ");
//    			System.err.println("Key: " + bindingService.getBestActiveBindingFormattedFor(cID));
//    		}
//    	}
//    	System.err.println("counter was " +i);   	
    	
    	bindingService.openKeyAssistDialog();    	    	
		return null;
	}

}
