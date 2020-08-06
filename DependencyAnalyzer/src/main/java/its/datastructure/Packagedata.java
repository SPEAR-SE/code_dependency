package its.datastructure;

import java.io.File;

public interface Packagedata {

	void setfile(File file);

	void setcomparefile(File comparefile);

	void setdistance(int distance);

	int getdistance();

	File getcomparefile();

	File getfile();

}