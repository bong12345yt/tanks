package com.tb.tanks.physic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class RecBody2D {
    private PointF parent;
    private PointF points[];
    private PointF savePoints[];
    private float w, h;
    private float angle;
    private Paint paint = new Paint();
    static private boolean isDrawBody;

    public RecBody2D(PointF[] points, PointF parent, float angle){
        this.points = points;
        this.parent = parent;
        this.angle = angle;
        this.savePoints = new PointF[4];
        for(int i = 0 ; i < 4; i++){
            savePoints[i] = new PointF(points[i].x, points[i].y);
        }
        w = (float)Math.sqrt((points[0].x - points[1].x)*(points[0].x - points[1].x) + (points[0].y - points[1].y)*(points[0].y - points[1].y));
        h = (float)Math.sqrt((points[1].x - points[2].x)*(points[1].x - points[2].x) + (points[1].y - points[2].y)*(points[1].y - points[2].y));
        paint.setColor(Color.YELLOW);
    }

    public static void setIsDrawBody(boolean draw){
        isDrawBody = draw;
    }

    public void setParentX(float x){
        parent.x = x;
    }

    public void setParentY(float y){
        parent.y = y;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        int i = 0;
        float rdi = (float)Math.toRadians(this.angle);
        float s = (float)Math.sin(rdi);
        float c = (float)Math.cos(rdi);
        for(PointF p:points){
            float xnew = savePoints[i].x * c - savePoints[i].y * s;
            float ynew = savePoints[i].x * s + savePoints[i].y * c;
            p.x = xnew;
            p.y = ynew;
            i++;
        }
    }

    public PointF[] getRealPoints(){
        PointF []realPoints = new PointF[4];
        realPoints[0] = new PointF(points[0].x + parent.x,  points[0].y + parent.y);

        realPoints[1] = new PointF(points[1].x + parent.x, points[1].y + parent.y);

        realPoints[2] = new PointF(points[2].x + parent.x, points[2].y + parent.y);

        realPoints[3] = new PointF(points[3].x + parent.x, points[3].y + parent.y);

        return realPoints;
    }

    public void Update(){

    }

    public void draw(Canvas g, float x, float y){
//        g.save();
//        g.translate(x + points[0].x, y + points[0].y);
//        g.rotate(this.angle, w/2, h/2);
//        g.drawRect(0, 0, w,  h, paint);
//        g.restore();
        if(isDrawBody){
            g.drawLine(x + points[0].x, y + points[0].y, x + points[1].x, y + points[1].y, paint);
            g.drawLine(x + points[1].x, y + points[1].y, x + points[2].x, y + points[2].y, paint);
            g.drawLine(x + points[2].x, y + points[2].y, x + points[3].x, y + points[3].y, paint);
            g.drawLine(x + points[3].x, y + points[3].y, x + points[1].x, y + points[1].y, paint);
            g.drawLine(x + points[3].x, y + points[3].y, x + points[0].x, y + points[0].y, paint);
        }

    }

    static public boolean  CheckCollision(RecBody2D obj1, RecBody2D obj2){
        PointF []realObj1 = obj1.getRealPoints();
        PointF []realObj2 = obj2.getRealPoints();
        PointF[] normals_obj1 = GetNormalsRec(realObj1[0], realObj1[1], realObj1[2], realObj1[3]);
        PointF[] normals_obj2 = GetNormalsRec(realObj2[0], realObj2[1], realObj2[2], realObj2[3]);


        boolean isSeparated = false;

        //use hexagon's normals to evaluate
        for (int i = 0; i < normals_obj1.length; i++) {
            float[] result_box1 = getMinMax(realObj1, normals_obj1[i]);
            float[] result_box2 = getMinMax(realObj2, normals_obj1[i]);

            isSeparated = (result_box1[1] < result_box2[0]) || (result_box2[1] < result_box1[0]);
            //console.log(result_box1);
            if (isSeparated) break;
        }
        //use triangle's normals to evaluate
        if (!isSeparated) {
            for (int j = 1; j < normals_obj2.length; j++) {
                float[] result_P1 = getMinMax(realObj1, normals_obj2[j]);
                float[] result_P2 = getMinMax(realObj2, normals_obj2[j]);

                isSeparated = result_P1[1] < result_P2[0] || result_P2[1] < result_P1[0];
                if (isSeparated) break;
            }
        }

        if (isSeparated){
            //System.out.println("not collision!");
            return false;
        }
        else{
            //System.out.println("collision!");
            return true;
        }
    }

    static public boolean  CheckCollision2(RecBody2D obj1, RecBody2D obj2){
        PointF []realObj1 = obj1.getRealPoints();
        PointF []realObj2 = obj2.getRealPoints();
        PointF[] normals_obj1 = GetNormalsRec(realObj1[0], realObj1[1], realObj1[2], realObj1[3]);
        PointF[] normals_obj2 = GetNormalsRec(realObj2[0], realObj2[1], realObj2[2], realObj2[3]);


        //results of P, Q
        float[] result_P1 = getMinMax(realObj1, normals_obj1[1]);
        float[] result_P2 = getMinMax(realObj2, normals_obj1[1]);
        float[] result_Q1 = getMinMax(realObj1, normals_obj1[0]);
        float[] result_Q2 = getMinMax(realObj2, normals_obj1[0]);

        //results of R, S
        float[] result_R1 = getMinMax(realObj1, normals_obj2[1]);
        float[] result_R2 = getMinMax(realObj2, normals_obj2[1]);
        float[] result_S1 = getMinMax(realObj1, normals_obj2[0]);
        float[] result_S2 = getMinMax(realObj2, normals_obj2[0]);

        boolean separate_p = result_P1[1] < result_P2[0] || result_P2[1] < result_P1[0];
        boolean separate_Q = result_Q1[1] < result_Q2[0] || result_Q2[1] < result_Q1[0];
        boolean separate_R = result_R1[1] < result_R2[0] || result_R2[1] < result_R1[0];
        boolean separate_S = result_S1[1] < result_S2[0] || result_S2[1] < result_S1[0];

        boolean isSeparated = false;
        isSeparated = separate_p || separate_Q || separate_R || separate_S;
        if (isSeparated) return false;
        else return true;

    }

    static public float dotProduct(PointF p1, PointF p2){
        return (p1.x * p2.x + p1.y * p2.y);
    }

    public static PointF[] GetNormalsRec(PointF pos1, PointF pos2, PointF pos3, PointF pos4) {

        PointF vec1 = new PointF(pos2.x - pos1.x,pos2.y - pos1.y);
        PointF normalv1 = new PointF(-vec1.y, vec1.x );

        PointF vec2 = new PointF(pos3.x - pos2.x,pos3.y - pos2.y);
        PointF normalv2 = new PointF(-vec2.y, vec2.x);

        PointF vec3 = new PointF( pos4.x - pos3.x, pos4.y - pos3.y);
        PointF normalv3 = new PointF(-vec3.y, vec3.x );

        PointF vec4 = new PointF(pos1.x - pos4.x, pos1.y - pos4.y);
        PointF normalv4 = new PointF(-vec4.y, vec4.x);

        PointF[] normals = new PointF[4];
        normals[0] = normalv1; normals[1] = normalv2; normals[2] = normalv3; normals[3] = normalv4;
        return normals;
    }


    static public float[] getMinMax(PointF[] ps, PointF axis) {
        PointF[] vecs_box = ps;

        float min_proj_box = dotProduct(vecs_box[1], axis); float min_dot_box = 1f;
        float max_proj_box = dotProduct(vecs_box[1], axis); float max_dot_box = 1f;

        for (int j = 0; j < vecs_box.length; j++) {
            float curr_proj = dotProduct(vecs_box[j], axis);
            //select the maximum projection on axis to corresponding box corners
            if (min_proj_box > curr_proj) {
                min_proj_box = curr_proj;
                min_dot_box = j;
            }
            //select the minimum projection on axis to corresponding box corners
            if (curr_proj > max_proj_box) {
                max_proj_box = curr_proj;
                max_dot_box = j;
            }
        }

        float []min_max_proj = new float[2];
        min_max_proj[0] = min_proj_box;
        min_max_proj[1] = max_proj_box;
        return min_max_proj;
    }

}
