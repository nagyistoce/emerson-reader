//package org.daisy.reader.model.z2005;
//
//import java.util.Iterator;
//
//import org.daisy.reader.model.smil.AudioMediaObject;
//import org.daisy.reader.ncx.INcxItemMatcher;
//import org.daisy.reader.ncx.NcxItem;
//
//public class Z2005NcxItemMatcher implements INcxItemMatcher {
//
//	public NcxItem getNcxItem(Iterator<?> iterator, AudioMediaObject phrase) {
//		NcxItem navItem = null;
//		/*
//		 * This gets a little messy since we do not know off the bat to where in the SMIL tree an
//		 * NCX URI points, we need to look at the phrases parents as well 		
//		 */
//		final String targetFileName = phrase.getParentContainer().getParentFile().getLocalName();
//		final String targetAudioID = phrase.getID();
//		final String targetParentTimeContainerID = phrase.getParentContainer().getID();
//		String targetParentParentTimeContainerID = null;
//		if(phrase.getParentContainer().getParentContainer()!=null)
//			targetParentParentTimeContainerID = phrase.getParentContainer().getParentContainer().getID();
//				
//		while(iterator.hasNext()) {
//			navItem = (NcxItem) iterator.next();
//			//TODO should use relative URI instead of filelocalname
//			if(targetFileName.equals(navItem.getTargetFileLocalName())) {
//				String targetID = navItem.getTargetFragment();
//				if(targetAudioID!=null 
//						&& targetAudioID.equals(targetID)) return navItem;
//				if(targetParentTimeContainerID!=null 
//						&& targetParentTimeContainerID.equals(targetID)) return navItem;
//				if(targetParentParentTimeContainerID!=null 
//						&& targetParentParentTimeContainerID.equals(targetID)) return navItem;			 					
//			} 
//		}
//		
//		return null;
//	}
//
//}
