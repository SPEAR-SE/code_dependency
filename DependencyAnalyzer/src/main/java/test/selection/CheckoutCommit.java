package test.selection;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

import configuration.Constant;


public class CheckoutCommit {
	
	public  boolean  checkout_a_commit_file(String commit,  String projectdir)  {
		Boolean checked = false; 
		try {
			Git git = Git.open(new File( projectdir ));
		    // CheckoutCommand checkout = git.checkout().setName(commit);
			CheckoutCommand checkout = git.checkout().setCreateBranch(true).setName(commit).setStartPoint(commit);
			checkout.call();
			int sleeptime = 0;
			while(checkout.getResult()== null) {			 
				Thread.sleep(2);		 
				sleeptime +=1;
				if(sleeptime > 600) {
					System.out.println("checkout takes more than an hour, please check what's wrong.");
					break;
				}
			}
			// add 10s more to make sure it is checked out.			 
			//	Thread.sleep(10);			 
 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RefAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRefNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CheckoutConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return checked;
	}
	
 
	
//	public static void main(String[] args) {
//		String projectdir = "/Users/zipeng/Projects/10project/kafka";
//		File filepath = new File("/Users/zipeng/Downloads/test/A.java");
//		String commit = "464aca362";
//		CheckoutCommit cc = new CheckoutCommit();
//		cc.checkout_a_commit_file(commit, projectdir);
//	}

}
