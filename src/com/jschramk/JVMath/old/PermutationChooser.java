package core.old;

import java.util.*;

@Deprecated
public class PermutationChooser implements Iterable<int[]> {

  private final List itemList;
  private final List matchList;
  private final Matcher matcher;

  public <T> PermutationChooser(List<T> itemList, List<T> matchList, Matcher<T> matcher) {
    this.itemList = itemList;
    this.matchList = matchList;
    this.matcher = matcher;
  }

  private static void incrementMins(int[] mins, int[] lastMatch, int max) {

    System.arraycopy(lastMatch, 0, mins, 0, lastMatch.length);

    for (int i = mins.length - 1; i >= 0; i--) {

      if (++mins[i] >= max && i > 0) {
        mins[i] = 0;
      } else {
        break;
      }

    }

  }

  public static <T> Set<T> commonElements(Set<T> s1, Set<T> s2) {
    Set<T> common = new HashSet<>();
    for (T t : s1) {
      if (s2.contains(t)) {
        common.add(t);
      }
    }
    return common;
  }

  public static <T> int[] getMatchArray(List<T> itemList, List<T> matchList, Matcher<T> matcher,
      int[] mins) {

    assert itemList != null;
    assert matchList != null;

    int numMatched = 0;
    int[] matches = new int[matchList.size()];
    Set<Integer> usedPositions = new HashSet<>();

    for (int i = 0; i < matchList.size(); ) {

      boolean foundMatch = false;

      for (int j = mins[i]; j < itemList.size(); j++) {

        if (usedPositions.contains(j)) {
          continue;
        }

        if (matcher.match(itemList.get(j), matchList.get(i), j, i)) {

          matches[i] = j;
          numMatched++;

          usedPositions.add(j);
          foundMatch = true;
          break;
        }

      }

      if (!foundMatch) {

        if (i == 0) {

          // if first match item could not find match, return null
          return null;

        } else {

          // backtrack

          i--;

          // get index of matched item by removing it
          int j = matches[i];

          // remove it from list of used items
          usedPositions.remove(j);
          numMatched--;

          // notify matcher of mismatch of previously matched items
          //matcher.onMismatch(j, i, true);

          // increment minimum starting pos for match item
          mins[i]++;

          //System.out.println("increment min " + i + ": " + mins[i]);

          // clear minimum positions of remaining match items
          for (int min = i + 1; min < mins.length; min++) {
            mins[min] = 0;
          }

        }

      } else {
        i++;
      }

    }

    if (numMatched < matchList.size()) {
      return null;
    }

    return matches;

  }

  public static <T> List<int[]> getAllMatchArrays(List<T> itemList, List<T> matchList,
      Matcher<T> matcher) {

    int[] mins = new int[matchList.size()];

    List<int[]> permutations = new ArrayList<>();

    while (true) {

      int[] map = getMatchArray(itemList, matchList, matcher, mins);

      if (map == null) {
        break;
      }

      //System.out.println("map: " + Arrays.toString(map));

      mins = Arrays.copyOf(map, map.length);

      //System.out.println("mins0: "+Arrays.toString(mins));

      for (int i = mins.length - 1; i >= 0; i--) {

        if (++mins[i] >= itemList.size() && i > 0) {
          //System.out.println("resetting i = "+i + ", was "+mins[i]);
          mins[i] = 0;
        } else {
          break;
        }

      }

      //System.out.println("mins1: "+Arrays.toString(mins));


      permutations.add(map);

    }

    return permutations;

  }

  public int[] getFirst() {
    return getNext(new int[matchList.size()]);
  }

  private int[] getNext(int[] mins) {

    assert itemList != null;
    assert matchList != null;

    int numMatched = 0;
    int[] matches = new int[matchList.size()];
    Set<Integer> usedPositions = new HashSet<>();

    for (int i = 0; i < matchList.size(); ) {

      boolean foundMatch = false;

      for (int j = mins[i]; j < itemList.size(); j++) {

        if (usedPositions.contains(j)) {
          continue;
        }

        if (matcher.match(itemList.get(j), matchList.get(i), j, i)) {
          // notify matcher of match
          //matcher.onMatch(j, i);

          matches[i] = j;
          numMatched++;

          usedPositions.add(j);
          foundMatch = true;
          break;
        }

      }

      if (!foundMatch) {

        if (i == 0) {

          // if first match item could not find match, return null
          return null;

        } else {

          // backtrack

          i--;

          // get index of matched item by removing it
          int j = matches[i];

          // remove it from list of used items
          usedPositions.remove(j);
          numMatched--;

          // notify matcher of mismatch of previously matched items
          //matcher.onMismatch(j, i, true);

          // increment minimum starting pos for match item
          mins[i]++;

          //System.out.println("increment min " + i + ": " + mins[i]);

          // clear minimum positions of remaining match items
          for (int min = i + 1; min < mins.length; min++) {
            mins[min] = 0;
          }

        }

      } else {
        i++;
      }

    }

    if (numMatched < matchList.size()) {
      return null;
    }

    return matches;

  }

  public List<int[]> getAll() {

    int[] mins = new int[matchList.size()];

    List<int[]> permutations = new ArrayList<>();

    while (true) {

      int[] lastMatch = getNext(mins);

      if (lastMatch == null) {
        break;
      }

      incrementMins(mins, lastMatch, itemList.size());

      permutations.add(lastMatch);

    }

    return permutations;

  }

  @Override public Iterator<int[]> iterator() {
    return new PermutationIterator(this);
  }

  public interface Matcher<T> {
    boolean match(T item, T match, int itemIndex, int matchIndex);
  }


  private static class PermutationIterator implements Iterator<int[]> {

    private final PermutationChooser permutationChooser;
    private final int[] mins;
    int[] next;

    public PermutationIterator(PermutationChooser permutationChooser) {
      this.permutationChooser = permutationChooser;
      mins = new int[permutationChooser.matchList.size()];

    }

    @Override public boolean hasNext() {

      next = permutationChooser.getNext(mins);

      if (next != null) {
        PermutationChooser.incrementMins(mins, next, permutationChooser.itemList.size());
      }

      return next != null;
    }

    @Override public int[] next() {
      return next;
    }

  }

}
