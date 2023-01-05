package com.vivo.internet.moonbox.service.common.utils;

/**
 * Pair
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/10/29 15:19
 */
public class Pair<L,R> {

    private L left;

    private R right;

    public Pair(L l, R r){
        this.left =l;
        this.right =r;
    }

    public Pair(){
    }


    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
