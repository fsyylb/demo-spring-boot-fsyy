package com.fsyy.demo.test;

import io.grpc.examples.routeguide.*;
import io.grpc.stub.StreamObserver;

public class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase {
    @Override
    public void getFeature(Point request, StreamObserver<Feature> responseObserver) {

    }

    @Override
    public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {

    }

    @Override
    public StreamObserver<Point> recordRoute(StreamObserver<RouteSummary> responseObserver) {
        return null;
    }

    @Override
    public StreamObserver<RouteNote> routeChat(StreamObserver<RouteNote> responseObserver) {
        return null;
    }
}
