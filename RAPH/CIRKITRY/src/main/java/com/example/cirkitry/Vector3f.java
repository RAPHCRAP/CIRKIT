package com.example.cirkitry;




class Vector3f {
    public double x, y, z;

    // Constructor
    public Vector3f(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Add two vectors
    public Vector3f add(Vector3f v) {
        return new Vector3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    // Subtract two vectors
    public Vector3f subtract(Vector3f v) {
        return new Vector3f(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    // Scale vector by a scalar
    public Vector3f scale(double s) {
        return new Vector3f(this.x * s, this.y * s, this.z * s);
    }

    // Normalize vector (make length 1)
    public Vector3f normalize() {
        double len = length(this);
        if (len == 0) return new Vector3f(0, 0, 0);
        return new Vector3f(this.x / len, this.y / len, this.z / len);
    }

    public Vector3f round(int decimals) {
    double factor = Math.pow(10, decimals);

    double rx = Math.round(this.x * factor) / factor;
    double ry = Math.round(this.y * factor) / factor;
    double rz = Math.round(this.z * factor) / factor;

    return new Vector3f(rx, ry, rz);
}

    // Cross product: this × v
    public Vector3f cross(Vector3f v) {
        return new Vector3f(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x
        );
    }

    // Dot product: this · v
    public double dot(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public void set(double x, double y, double z)
    {
        this.x =x;
        this.y =y;
        this.z =z;
    }

    // Length of this vector
    public double length() {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }

    // Static utility methods (optional)
    public static double dot(Vector3f a, Vector3f b) {
        return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    public static double length(Vector3f v) {
        return Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
    }



    @Override
    public String toString() {
        return String.format("Vector3f(%.3f, %.3f, %.3f)", x, y, z);
    }
}

