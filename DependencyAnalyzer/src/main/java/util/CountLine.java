package util;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

public class CountLine {

	public int countLineinCU(CompilationUnit cu) {
		int beginline = 0;
		int lastline = 0;
		int loc = 0;
		int commentline = 0;
		if(cu!=null) {
			if(cu.getBegin().isPresent()) {
				beginline = cu.getBegin().get().line;
			}
			
			if(cu.getEnd().isPresent()) {
				lastline = cu.getEnd().get().line;
			}
					
			List<Comment> comments = cu.getComments();
			for(Comment comment: comments) {
				int commentbegin=0;
				int commentend=0;
				if(comment.getBegin().isPresent()) {
					commentbegin = comment.getBegin().get().line;
				}
				if(comment.getEnd().isPresent()) {
					commentend = comment.getEnd().get().line;
				}
				commentline = (commentend - commentbegin +1) + commentline;
			}
			
			loc = lastline - beginline - commentline;
			return loc;
		}
		else {
			return 0;
		}
	}

}


