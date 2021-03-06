
package com.cyberark;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.GraphWalker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.model.Edge;
import org.graphwalker.java.test.TestBuilder;
import org.graphwalker.core.condition.TimeDuration;
import org.graphwalker.core.condition.VertexCoverage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BstTest extends ExecutionContext implements BstModel {

  public final static Path MODEL_PATH = Paths.get("com/cyberark/BstModel.json");
  private Bst<Integer> bst;
  private ArrayList<Integer> vals;      // values to be inserted in the tree
  private ArrayList<Integer> fakeVals;  // values that are not inserted in the tree
  private HashSet<Integer> inTree;      // the current values in the tree
  private Random rand;
  private boolean result;


  @Override
  public void e_Add()
  {
    System.out.println( "e_Add" );
    int val = vals.get(rand.nextInt(vals.size())); // choose value at random
    bst.add(val); // add the value to the tree
    /**
     * add that value to a hash-set
     * Binary-Serach Tree doesn't hold duplicate values
     * if the value that is chosen at random is already in the tree
     * it will not be added.
     * We then perform the 'add' operation also on the hash-set: inTree,
     * that also don't accept duplicate values.
     * If the value that is chosen at random is already in the tree, it ia also
     * already in inTree. This duplicate then will not be added by Bst.e_Add,
     * and it will also not be added by inTree.
     * Bst should always be at the same state as inTree in terms of
     * the values both have, and this is what you have to check in the vertices
     */
    inTree.add(val);
  }

  @Override
  public void e_Find()
  {
    System.out.println( "e_Find" );
    //convert HashSet to an array to fetch element by random index
    Integer[] arrInTreeVals = inTree.toArray( new Integer[inTree.size()] );
    int randomIndex = rand.nextInt(inTree.size());
    result = bst.find(arrInTreeVals[randomIndex]);
  }


  @Override
  public void e_FindFakeVal()
  {
    System.out.println( "e_FindFakeVal" );
    result = bst.find(fakeVals.get(rand.nextInt(fakeVals.size())));
  }


  @Override
  public void e_Init()
  {
    System.out.println( "e_Init" );
    bst = new Bst<Integer>();
    // vals - values to be added to the tree
    vals = new ArrayList<Integer>(Arrays.asList(1, 3, 4, 6, 7, 8, 10, 13, 14));
    fakeVals = new ArrayList<Integer>(Arrays.asList(21, 23, 24, 26, 27, 28, 30, 33, 34));
    // inTree - values that expected to be in the tree
    inTree = new HashSet<Integer>();
    rand = new Random();
    result = false;
  }


  @Override
  public void v_Added()
  {
    System.out.println( "v_Added" );
    assertEquals(inTree.size(), bst.nodes().size());
  }


  @Override
  public void v_Found()
  {
    System.out.println( "v_Found" );
    assertTrue(result, "Find failed!");
  }


  @Override
  public void v_NotFound()
  {
    System.out.println( "v_NotFound" );
    assertFalse(result, "Found a faked value!");
  }


  @Override
  public void v_Start()
  {
    System.out.println( "v_Start" );
  }


  @Override
  public void v_VerifyInitialState()
  {
    System.out.println( "v_VerifyInitialState" );
    assertNotNull(bst);
  }

  /** *******************  TESTS RUNNERS  ********************* */

  @Test
    public void runSmokeTest() {
        new TestBuilder()
                .addContext(new BstTest().setNextElement(new Edge().setName("e_Init").build()),
                        MODEL_PATH,
                        new AStarPath(new ReachedVertex("v_Found")))
                .execute();
    }

    @Test
    public void runFunctionalTest() {
        new TestBuilder()
                .addContext(new BstTest().setNextElement(new Edge().setName("e_Init").build()),
                        MODEL_PATH,
                        new RandomPath(new EdgeCoverage(100)))
                .addContext(new BstTest().setNextElement(new Edge().setName("e_Init").build()),
                        MODEL_PATH,
                        new RandomPath(new VertexCoverage(100)))
                .execute();
    }

    @Test
    public void runStabilityTest() {
        new TestBuilder()
                .addContext(new BstTest().setNextElement(new Edge().setName("e_Init").build()),
                        MODEL_PATH,
                        new RandomPath(new TimeDuration(1, TimeUnit.SECONDS)))
                .execute();
    }
}
