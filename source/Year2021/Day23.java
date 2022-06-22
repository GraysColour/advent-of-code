package Year2021;

/*
  The idea:
    Consider the initial positions of all the amphipods
    as the first "state" with a cost of 0.
    Then each move of any amphipods becomes
    a new state with the added cost of the move.

    Since moves can only be of the tree:
      a) from a room to the hallway
      b) from a room to another room
      c) from the hallway to a room
    each new move/state can be based on the current state.

    At the initial state, there are 28 possible moves:
      The top amphipods of each of the 4 rooms can move
      to any of the 7 free positions in the hallway.
    Those 28 new states can then be processed
    with their possible new states looking at a), b) and c).

    Looking at the layout, the hallway have only 7 free positions.
    The rooms being named 0 to 3:

        #############
        #01 2 3 4 56#  <-- hallway postions
        ### # # # ###
          # # # # #
          #########
           0 1 2 3     <-- room index

    At each move of a), moves of type b) and c) can be
    performed without "splitting" them into further possible moves.
    The amphipods must go into their home rooms at some
    point and postponing that doesn't change the total cost.
    So any of the 28 possible moves at any state, will remain just 28
    possible moves including any subsequent move of b) anc c).

    For example:
      One of the possible 28 moves of a)
      is moving B from room 2 to hallway position 2.
      At this state, moving C from room 1 to
      its home room 2 doesn't change the outcome of
      whether or not moving B to that position in the hallWay
      will result in being able to put all amphipods
      into their home rooms, nor will it change the
      total cost, if this move results in success:

        #############      #############
        #...B.......#      #...B.......#
        ###B#C#.#D###      ###B#.#C#D###
          #A#D#C#A#          #A#D#C#A#
          #########          #########

    This reduces the states to at the most
    28 new branches/states for each state.

    The algorithm becomes:
      1. add the initial state to a queue
      2. pull an element from the queue and:
         - create (up to 28) new states moving from room to hallway
         - for each of those states:
           if possible, move any amphipods to it's home room
         - if all amphipods are home, then the state is final
         - add non-final states to the queue.

    ### Dijkstra's versus finding all solutions
      If using Dijktra's algorithm, which uses a priority queue,
      polling the next state as the one with the lowest cost,
      there's a risk of getting a wrong lowest cost
      if the algorithm stops at a success.
      Imagine these two (contrived) states in the queue:

        Current cost: 3000     Current cost: 3010
          #############          #############
          #.......D...#          #...A.......#
          ###A#B#.#C###          ###B#.#C#D###
            #A#B#C#D#              #A#B#C#D#
            #########              #########

      The first one would require moving C to hallWay position 5.
      Then moving D to its home room, and finally moving C to its home room.
      Added cost of that is: 200 + 2000 + 400 = 2600.
      Total cost would be: 3000 + 2600 = 5600

      The second would get a total cost of: 3010 + 20 + 2 + 40 = 3072
      which is obviously much lower.

      If using a priority queue, one way to solve it
      could be to keep a set of success/solutions and compare the
      lowest cost of those to the next element in the queue.
      Not until the next element of the queue has a cost
      that's higher, can the lowest cost be determined.
      However, this can result in never finding the lowest cost,
      since any state that invovles move of 'D' will be last in the queue.
      Those 'D' will be in the hallway, and still needing to go into their homes.
      The last moves of 'D' into the homes are
      included in the lowest cost in the success-set,
      and hence will always be greater than any first element on the queue.

      Another way to solve this could be to not use Dijkstra's algorithm
      at all, but simply compute all solutions and then find the lowest cost.
      That's the solution used in this implementation.

    ### Implementation specifics of amphipods being home
      Instead of using the characters for the amphipods,
      when amphipods are correctly "home" a different
      characters is used. Here a '.'.

      For example: This is the home of 'A's:

          # #      #A#
          #.#      #C#
          ###      ###

      The first one has one 'A' that's home and an empty spot on top.
      The second one has no 'A'-amphipods at home.
      This makes it somewhat easier implementation-wise,
      since any characters in a home, must be moved to the hallway.
      Only '.' must stay.

      An int[] array is kept to keep track of the
      amount of amphipods that are home.
      For example [2,1,0,3] will signal that
        - 2 'A'-amphipods are home.
        - 1 'B'-amphipods is home.
        - no 'C'-amphipods are home.
        - 3 'D'-amphipods are home.
*/

import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;

import absbase.DayX;
import util.Result;

public class Day23 extends DayX {

  private int amphipodRows = 2;
  private AmpState startPart1;
  private AmpState startPart2;
  private AmpState initial;    // either startPart1 or startPart2

  private char amphipodHome = '.';

  // -----
  public static void main(String[] args){
    new Day23(args).doIt();
  }

  public Day23(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    char[][] rooms = new char[4][this.amphipodRows];

    String[] splitInput = input.split("\\R");

    // rooms become: [[B, A], [C, D], [B, C], [D, A]]
    for (int i = 0; i < 2; i++) { // rows 2 & 3
      String level = splitInput[i+2];
       for (int j = 0; j < 4; j++) {
        rooms[j][i] = level.charAt(j*2+3); // 3, 5, 7, 9
      }
    }

    //  #D#C#B#A# <-- second row for part2
    //  #D#B#A#C# <--  third row for part2
    char[][] roomsPart2 = new char[4][this.amphipodRows + 2];
    roomsPart2[0] = new char[]{rooms[0][0], 'D', 'D', rooms[0][1]};
    roomsPart2[1] = new char[]{rooms[1][0], 'C', 'B', rooms[1][1]};
    roomsPart2[2] = new char[]{rooms[2][0], 'B', 'A', rooms[2][1]};
    roomsPart2[3] = new char[]{rooms[3][0], 'A', 'C', rooms[3][1]};

    this.startPart1 = createStartAmpState(rooms);
    this.startPart2 = createStartAmpState(roomsPart2);
    this.initial = this.startPart1;
  }

  private AmpState createStartAmpState(char[][] rooms) {
    // rooms become: [[B, .], [C, D], [B, .], [D, A]]
    // homes become: [1, 0, 1, 0], since one A and one C is already home.
    int[] homes = new int[4];
    for (int j = 0; j < 4; j++) {
      char amphipod = toAmphipodMap.get(j);
      for (int i = rooms[j].length - 1; i > 0; i--) {
        if (rooms[j][i] != amphipod) {
          break;
        } else {
          rooms[j][i] = this.amphipodHome;
          homes[fromAmphipodMap.get(amphipod).roomIndex] += 1;
        }
      }
    }

    return new AmpState(rooms, homes, new char[7]);
  }


  // -----
  public Result solve(String input) {
    if (this.initial == null) {
      setup(input);
    }

    int minimumCost = Integer.MAX_VALUE;

    Queue<AmpState> ampStateQueue = new LinkedList<>();
    ampStateQueue.add(this.initial);

    AmpState current = null;
    while ((current = ampStateQueue.poll()) != null) {
      if (minimumCost < current.cost) {
        continue;  // no need to look at a state that is already worse
      }

      for (int roomIndex = 0; roomIndex < 4; roomIndex++) {

        // get highest Amphipod in room
        int position = nextAmphipodPositionInRoom(current.rooms[roomIndex]);
        if (position > -1) {

          char amphipod = current.rooms[roomIndex][position];

          for (int hallNumber = 0; hallNumber < current.hallWay.length; hallNumber++) {

            if (clearPathToHall(hallNumber, roomIndex, current)) {

              // move from room to hallway
              AmpState newAmpState = current.clone();
              newAmpState.rooms[roomIndex][position] = 0;
              newAmpState.hallWay[hallNumber] = amphipod;
              newAmpState.cost += distanceFromToHall(roomIndex, position, hallNumber)
                                    * fromAmphipodMap.get(amphipod).cost;

              // move from hallway to home once
              newAmpState = moveFromHallToHomeroom(newAmpState);
              AmpState progressedAmpState = newAmpState;

              do { // keep moving into homeRooms until there's no change
                newAmpState = progressedAmpState;
                // move from room to homeRoom
                progressedAmpState = moveFromRoomToHomeroom(progressedAmpState);
                // move from hallway to home
                progressedAmpState = moveFromHallToHomeroom(progressedAmpState);
              } while (progressedAmpState.cost != newAmpState.cost);

              if (!newAmpState.isSuccess()) {
                ampStateQueue.add(newAmpState);
              } else if (minimumCost > newAmpState.cost) {
                // find the lowest cost
                minimumCost = newAmpState.cost;
              }

            }
          }
        }
      }
    }

    return Result.createResult(minimumCost);
  }

  // ----
  private AmpState moveFromHallToHomeroom(AmpState ampState){
    AmpState newAmpState = ampState; // initialize

    do {
      ampState = newAmpState; // the the old the newest one
      for (int hallNumber = 0; hallNumber < ampState.hallWay.length; hallNumber++) {

        if (ampState.hallWay[hallNumber] != 0) { // there's an amphipod there

          char amphipod = ampState.hallWay[hallNumber];
          int homeRoom = fromAmphipodMap.get(amphipod).roomIndex;

          if (clearPathFromHall(hallNumber, homeRoom, ampState)) {

            int position = homeIndexClear(ampState.rooms[homeRoom]);
            if (position > -1) { // the homeRoom is clear to move into

              // make the move
              newAmpState = ampState.clone();
              newAmpState.hallWay[hallNumber] = 0;
              newAmpState.rooms[homeRoom][position] = this.amphipodHome;
              newAmpState.atHome[homeRoom] += 1;
              newAmpState.cost += distanceFromToHall(homeRoom, position, hallNumber)
                                    * fromAmphipodMap.get(amphipod).cost;
            }
          }
        }
      }
    } while (ampState.cost != newAmpState.cost); // something changed

    return newAmpState;
  }

  // ----
  private AmpState moveFromRoomToHomeroom(AmpState ampState){
    AmpState newAmpState = ampState; // initialize

    do {
      ampState = newAmpState; // the the old the newest one
      for (int roomIndex = 0; roomIndex < ampState.rooms.length; roomIndex++) {

        int positionFrom = nextAmphipodPositionInRoom(ampState.rooms[roomIndex]);
        if (positionFrom > -1) { // there is an amphipod to move

          char amphipod = ampState.rooms[roomIndex][positionFrom];
          int homeRoom = fromAmphipodMap.get(amphipod).roomIndex;

          if (clearPathFromRoom(roomIndex, homeRoom, ampState)) {

            int positionTo = homeIndexClear(ampState.rooms[homeRoom]);
            if (positionTo > -1) { // the homeRoom is clear to move into

              // make the move
              newAmpState = ampState.clone();
              newAmpState.rooms[roomIndex][positionFrom] = 0;
              newAmpState.rooms[homeRoom][positionTo] = this.amphipodHome;
              newAmpState.atHome[homeRoom] += 1;
              newAmpState.cost += distanceRoomToRoom(roomIndex, positionFrom,
                                                     homeRoom, positionTo)
                                    * fromAmphipodMap.get(amphipod).cost;
            }
          }
        }
      }
    } while (ampState.cost != newAmpState.cost); // something changed

    return newAmpState;
  }

  // ----
  private int distanceRoomToRoom(int roomIndex1, int position1,
                                 int roomIndex2, int position2) {
    return position1 + 1 +
           Math.abs(roomIndex1 - roomIndex2) * 2 +
           position2 + 1;
  }

  // ----
  private int[][] roomHallwayDistance =
               {{2,1,1,3,5,7,8},
                {4,3,1,1,3,5,6},
                {6,5,3,1,1,3,4},
                {8,7,5,3,1,1,2}};

  private int distanceFromToHall(int roomIndex, int position, int hallNumber) {
    return roomHallwayDistance[roomIndex][hallNumber] + 1 + position;
  }

  // ----
  private int nextAmphipodPositionInRoom(char[] room) {
    for (int position = 0; position < room.length; position++) {
      if (Character.isLetter(room[position])) {
        return position;
      }
    }
    return -1;
  }

  // ----
  private int homeIndexClear(char[] room) {
    for (int position = 0; position < room.length; position++) {
      if (Character.isLetter(room[position])) { // an amphipod not at home
        return -1;
      } else if (room[position] == this.amphipodHome) { // an amphipod at home
        return position - 1;
      }
    }
    return room.length - 1;  // the room is empty
  }

  // ----
  private boolean clearPathFromHall(int hallNumber, int roomIndex, AmpState ampState) {
    return clearPathFromToHall(hallNumber, roomIndex, ampState, false);
  }

  private boolean clearPathToHall(int hallNumber, int roomIndex, AmpState ampState) {
    return clearPathFromToHall(hallNumber, roomIndex, ampState, true);
  }

  private boolean clearPathFromToHall(int hallNumber,
                                      int roomIndex,
                                      AmpState ampState,
                                      boolean to) { // true if from room to hall
    char[] hallWay = ampState.hallWay;
    if (to && hallWay[hallNumber] != 0) { // if the space is already occupied
      return false;
    }

    // [01 2 3 4 56] <-- hallWay
    // [  0 1 2 3  ] <-- roomNumber

    if ((hallNumber == 0 && hallWay[1] == 0) || hallNumber == 1) {
      return clearPathFromRoom(0, roomIndex, ampState);
    }
    if ((hallNumber == 6 && hallWay[5] == 0) || hallNumber == 5) {
      return clearPathFromRoom(3, roomIndex, ampState);
    }

    if (hallNumber == 2) {
      if (roomIndex < 2) {
        return true;
      } else {
        return clearPathFromRoom(1, roomIndex, ampState);
      }
    }
    if (hallNumber == 3) {
      if (roomIndex > 0 && roomIndex < 3) {
        return true;
      } else {
        return clearPathFromRoom(roomIndex == 0 ? 1 : 2, roomIndex, ampState);
      }
    }
    if (hallNumber == 4) {
      if (roomIndex > 1) {
        return true;
      } else {
        return clearPathFromRoom(2, roomIndex, ampState);
      }
    }

    return false;
  }

  // ----
  private boolean clearPathFromRoom(int roomNumber, int roomIndex, AmpState ampState) {

    // [01 2 3 4 56] <-- hallWay
    // [  0 1 2 3  ] <-- roomNumber

    char[] hallWay = ampState.hallWay;

    if (hallWay[2] == 0 && hallWay[3] == 0 && hallWay[4] == 0) {
      return true;
    }
    if (hallWay[2] == 0 && hallWay[3] == 0
        && roomNumber < 3 && roomIndex < 3) {
      return true;
    }
    if (hallWay[3] == 0 && hallWay[4] == 0
        && roomNumber > 0 && roomIndex > 0) {
      return true;
    }
    if (hallWay[2] == 0
        && roomNumber < 2 && roomIndex < 2) {
      return true;
    }
    if (hallWay[3] == 0
        && roomNumber != 0 && roomNumber != 3
        && roomIndex != 0 && roomIndex != 3) {
      return true;
    }
    if (hallWay[4] == 0
        && roomNumber > 1 && roomIndex > 1) {
      return true;
    }

    return false;
  }

  // ----
  private Map<Integer, Character> toAmphipodMap
    = Map.of(0, 'A',
             1, 'B',
             2, 'C',
             3, 'D');

  // ----
  private Map<Character, Amphipod> fromAmphipodMap
    = Map.of('A', new Amphipod(0, 1),    // roomIndex, cost
             'B', new Amphipod(1, 10),
             'C', new Amphipod(2, 100),
             'D', new Amphipod(3, 1000));

  private class Amphipod {
    int roomIndex;
    int cost;
    Amphipod(int roomIndex, int cost) {
      this.roomIndex = roomIndex;
      this.cost = cost;
    }
  }

  private class AmpState implements Comparable<AmpState>{
    char[][] rooms;
    int[] atHome;
    char[] hallWay;
    int cost;

    AmpState(char[][] rooms,
             int[] atHome,     // [count of As, count of Bs, ...]
             char[] hallWay) {
      this.rooms = rooms;
      if (atHome.length != 4) {
        throw new AssertionError("atHome must have 4 slots");
      }
      this.atHome = atHome;
      if (hallWay.length != 7) {
        throw new AssertionError("hallWay must have 7 slots");
      }
      this.hallWay = hallWay;
      this.cost = 0;
    }

    boolean isSuccess() {
      if (Arrays.stream(atHome).sum() == 4 * Day23.this.amphipodRows){
        return true;
      }
      return false;
    }

    public int compareTo(AmpState that){
      return this.cost - that.cost;
    }

    @Override
    public AmpState clone(){
      char[][] copyRooms = new char[4][];
      for (int i = 0; i < copyRooms.length; i++) {
        char[] room = new char[Day23.this.amphipodRows];
        System.arraycopy(rooms[i], 0, room, 0, room.length);
        copyRooms[i] = room;
      }
      AmpState newAmpState
        = new AmpState(copyRooms,
                       Arrays.copyOf(atHome, atHome.length),
                       Arrays.copyOf(hallWay, hallWay.length));
      newAmpState.cost = this.cost;
      return newAmpState;
    }

    // for debugging purposes
    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      // top line:
      stringBuilder.append("#".repeat(13));
      stringBuilder.append("\n");
      // hallway
      stringBuilder.append("#");
      stringBuilder.append(hallWay[0]); stringBuilder.append(hallWay[1]);
      stringBuilder.append(" ");
      stringBuilder.append(hallWay[2]); stringBuilder.append(" ");
      stringBuilder.append(hallWay[3]); stringBuilder.append(" ");
      stringBuilder.append(hallWay[4]);
      stringBuilder.append(" ");
      stringBuilder.append(hallWay[5]); stringBuilder.append(hallWay[6]);
      stringBuilder.append("#");
      stringBuilder.append("\n");
      // homes
      for (int i = 0; i < rooms[0].length; i++) {
        stringBuilder.append(i == 0 ? "##" : "  ");
        stringBuilder.append("#");
        for (int j = 0; j < rooms.length; j++) {
          stringBuilder.append(rooms[j][i]);
          stringBuilder.append("#");
        }
        stringBuilder.append(i == 0 ? "##" : "  ");
        stringBuilder.append("\n");
    }
      stringBuilder.append("  ");
      stringBuilder.append("#".repeat(9));
      stringBuilder.append("  ");
      stringBuilder.append("\n");
      // atHome count
      stringBuilder.append("  "); stringBuilder.append("#");
      stringBuilder.append(atHome[0]); stringBuilder.append("#");
      stringBuilder.append(atHome[1]); stringBuilder.append("#");
      stringBuilder.append(atHome[2]); stringBuilder.append("#");
      stringBuilder.append(atHome[3]);
      stringBuilder.append("#"); stringBuilder.append("  ");
      // cost
      stringBuilder.append("\tcost: ");
      stringBuilder.append(cost);

      return stringBuilder.toString();
    }
  }


  // -----
  public Result solvePart2(String input) {
    if (this.startPart2 == null) {
      setup(input);
    }

    this.initial = this.startPart2;
    this.amphipodRows = 4;
    Result result = solve(input);
    this.initial = this.startPart1;
    this.amphipodRows = 2;

    return result;
  }
}
