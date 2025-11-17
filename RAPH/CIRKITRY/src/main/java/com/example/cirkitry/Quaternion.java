package com.example.cirkitry;


class Quaternion {
    public double w, x, y, z;

    public Quaternion() { // identity
        w = 1; x = 0; y = 0; z = 0;
    }

    public Quaternion(double w, double x, double y, double z) {
        this.w = w; this.x = x; this.y = y; this.z = z;
    }

    public Quaternion normalize() {
        double len = Math.sqrt(w*w + x*x + y*y + z*z);
        return new Quaternion(w/len, x/len, y/len, z/len);
    }

    public Quaternion multiply(Quaternion q) {
        double nw = w*q.w - x*q.x - y*q.y - z*q.z;
        double nx = w*q.x + x*q.w + y*q.z - z*q.y;
        double ny = w*q.y - x*q.z + y*q.w + z*q.x;
        double nz = w*q.z + x*q.y - y*q.x + z*q.w;
        return new Quaternion(nw, nx, ny, nz);
    }

    public Vector3f rotateVector(Vector3f v) {
        Quaternion vq = new Quaternion(0, v.x, v.y, v.z);
        Quaternion res = this.multiply(vq).multiply(conjugate());
        return new Vector3f(res.x, res.y, res.z);
    }

    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    public static Quaternion fromAxisAngle(Vector3f axis, double angleRad) {
        double half = angleRad / 2.0;
        double s = Math.sin(half);
        return new Quaternion(Math.cos(half), axis.x*s, axis.y*s, axis.z*s);
    }
}
