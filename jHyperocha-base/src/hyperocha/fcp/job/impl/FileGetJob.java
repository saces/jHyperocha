/**
 *   This file is part of JHyperochaFCPLib.
 *   
 *   Copyright (C) 2006  Hyperocha Project <saces@users.sourceforge.net>
 * 
 * JHyperochaFCPLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JHyperochaFCPLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JHyperochaFCPLib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
package hyperocha.fcp.job.impl;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.io.FCPInputStream;
import hyperocha.fcp.job.DataRequestJob;
import hyperocha.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * download a file the best way  includes black magic ritual and eating childs for finding the best way
 * @version   $Id$
 */
public class FileGetJob extends DataRequestJob {

	private File targetFile;

	public FileGetJob(String id, FreenetKey key, File target) {
		super(id, key);
		targetFile = target.getAbsoluteFile();
	}
//
//	@Override
//	public boolean doPrepare() {
//		// TODO: is targetFile a valid file name/target?
//		//        check for writable? but do not create bzw. dont leave the file after test!
//		return true;
//	}
	
	@Override
	protected void onSimpleProgress(boolean isFinalized, long totalBlocks,
			long requiredBlocks, long doneBlocks, long failedBlocks,
			long fatallyFailedBlocks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDataFound(FCPInputStream fis, long size) {
		try {
			boolean result = FileUtil.writeTo(fis, targetFile);
			System.out.println("Copytest: "+result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
