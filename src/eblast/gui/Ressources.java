/*
 * This file is part of eBlast Project.
 *
 * Copyright (c) 2011 eBlast
 *
 * eBlast is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eBlast is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eBlast.  If not, see <http://www.gnu.org/licenses/>.
 */

package eblast.gui;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * This is a class stocks all GUI constants (colors, icons, strings, ...)
 * 
 * @author David Dieulivol <david.dieulivol@gmail.com>
 * @author Denoréaz Thomas <thomas.denoreaz@thmx.ch>
 * 
 * @version 1.0 - 23.05.2011 - Initial version
 */
public final class Ressources {
	public static final class drawables {
        public static final ImageIcon open			= new ImageIcon("icons/16/open.png");
        public static final ImageIcon play			= new ImageIcon("icons/16/play.png");
        public static final ImageIcon pause			= new ImageIcon("icons/16/pause.png");
        public static final ImageIcon delete		= new ImageIcon("icons/16/delete.png");
        public static final ImageIcon download		= new ImageIcon("icons/32/download.png");
        public static final ImageIcon upload		= new ImageIcon("icons/32/upload.png");

        public static final ImageIcon file_eblast	= new ImageIcon("icons/32/eblast_file.png");
        public static final ImageIcon file_down		= new ImageIcon("icons/32/file_downloading.png");
        public static final ImageIcon file_up		= new ImageIcon("icons/32/file_uploading.png");
        public static final ImageIcon file_pause	= new ImageIcon("icons/32/file_pausing.png");
        public static final ImageIcon file_checking	= new ImageIcon("icons/32/file_checking.png");
	}
	
	public static final class colors {
		public static Color selected				= new Color(	0.0f,	0.0f,	0.1f,	0.2f	);
		public static Color zebraPattern_1			= new Color(	0.0f,	0.0f,	0.25f,	0.1f	);
		public static Color zebraPattern_2			= new Color(	1.0f,	1.0f,	1.0f,	0.1f	);
		
		public static final Color half_gray_start	= new Color(	0.0f,	0.0f,	0.0f,	0.2f	);
		public static final Color half_gray_end		= new Color(	0.0f,	0.0f,	0.0f,	0.6f	);
		public static final Color half_red_start	= new Color(	1.0f,	0.0f,	0.0f,	0.2f	);
		public static final Color half_red_end		= new Color(	1.0f,	0.0f,	0.0f,	0.6f	);
		public static final Color half_green_start	= new Color(	0.0f,	1.0f,	0.0f,	0.2f	);		
		public static final Color half_green_end	= new Color(	0.0f,	0.8f,	0.0f,	0.6f	);
		public static final Color half_blue_start	= new Color(	0.0f,	0.5f,	1.0f,	0.2f	);
		public static final Color half_blue_end		= new Color(	0.0f,	0.4f,	0.8f,	0.6f	);
		
		public static final Color red_start			= new Color(	0xFF8080	);
		public static final Color red_end			= new Color(	0xF83030	);
		public static final Color green_start		= new Color(	0xCBFECC	);		
		public static final Color green_end			= new Color(	0x66DF66	);
		public static final Color blue_start		= new Color(	0xCCE6FE	);
		public static final Color blue_end			= new Color(	0x66A2DF	);
	}
	
	public static final class numbers {
		public static final int timer_delay			= 1000;
	}
	
	public static final class strings {
		public static final String eblast			= "eBlast";
		public static final String open				= "Open";
		public static final String open_torrent		= "Open Torrent";
		public static final String close			= "Close";
		public static final String resume			= "Resume";
		public static final String pause			= "Pause";
		public static final String delete			= "Delete";
		public static final String name				= "Name";
		public static final String seeder			= "Seeder";
		public static final String leecher			= "Leecher";
		public static final String peers			= "Peers";
		public static final String downloaded		= "Downloaded";
		public static final String download_path	= "Download path";
		public static final String size				= "Size";
		public static final String percent_symb		= "%";
		public static final String general_info		= "General Info";
		public static final String tracker			= "Tracker";
		public static final String files			= "Files";
		public static final String author			= "Author";
		public static final String comment			= "Comment";
		public static final String date				= "Date";
		public static final String piece_count		= "# Pieces";
		public static final String piece_size		= "Piece size";
		public static final String file				= "File";
		public static final String configurations	= "Configurations";
		public static final String exit				= "Exit";
		public static final String ip				= "IP";
		public static final String client			= "Client";
		public static final String started			= "Started";
		public static final String stopped			= "Stopped";
		public static final String completed		= "Completed";
		public static final String checking			= "Checking";
		public static final String status			= "Status";
		public static final String last_update		= "Last update";
		public static final String torrent_file		= "Torrent File";
	}
}
