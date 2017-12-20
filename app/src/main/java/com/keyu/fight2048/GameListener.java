package com.keyu.fight2048;

/**
 * Created by focus on 2017/12/17.
 */

public interface GameListener {

   /**
    * 监听分数变化
    *
    * @param score
    */
   void  onScoreChange(int score);

   /**
    * 监听游戏结束
    */
   void onGameOver();
}
