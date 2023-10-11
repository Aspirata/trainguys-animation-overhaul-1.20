package com.trainguy9512.animationoverhaul.animation.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.trainguy9512.animationoverhaul.AnimationOverhaulMain;
import com.trainguy9512.animationoverhaul.util.data.TimelineGroupData;
import com.trainguy9512.animationoverhaul.util.data.TransformChannel;
import com.trainguy9512.animationoverhaul.util.math.RotationMatrix;
import com.trainguy9512.animationoverhaul.util.time.ChannelTimeline;
import com.trainguy9512.animationoverhaul.util.time.Easing;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Rotations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import org.joml.*;
import org.joml.Math;

public class MutablePartPose {
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public Quaternionf rotation;
    /*
    public float xRot = 0;
    public float yRot = 0;
    public float zRot = 0;
     */


    public static final MutablePartPose ZERO = fromPartPose(PartPose.ZERO);

    private MutablePartPose(float x, float y, float z, float xRot, float yRot, float zRot){
        this(x, y, z, new Quaternionf().rotationXYZ(xRot, yRot, zRot));
        this.setEulerRotation(new Vector3f(xRot, yRot, zRot));
    }

    private MutablePartPose(float x, float y, float z, Quaternionf rotation){
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
    }

    public Vector3f getTranslation(){
        return new Vector3f(this.x, this.y, this.z);
    }

    public static MutablePartPose fromTranslation(float x, float y, float z){
        return fromTranslationAndRotation(x, y, z, 0, 0, 0);
    }

    public static MutablePartPose fromRotation(float xRot, float yRot, float zRot){
        return fromTranslationAndRotation(0, 0, 0, xRot, yRot, zRot);
    }

    public static MutablePartPose fromTranslationAndRotation(float x, float y, float z, float xRot, float yRot, float zRot){
        return new MutablePartPose(x, y, z, xRot, yRot, zRot);
    }

    public static MutablePartPose fromTranslationAndRotation(float x, float y, float z, Quaternionf rotation){
        return new MutablePartPose(x, y, z, rotation);
    }

    /*
    public static Quaternionf getQuaternionFromEuler(Vector3f eulerRotation){
        return new Quaternionf().rotationXYZ(eulerRotation.x(), eulerRotation.y(), eulerRotation.z());
    }

    public static Vector3f getEulerFromQuaternion(Quaternionf quaternionf){
        return quaternionf.getEulerAnglesZYX(new Vector3f());
    }

     */

    public Quaternionf getRotation(){
        return this.getCopy().rotation;
    }

    public void setRotation(Quaternionf quaternionf){
        this.rotation = quaternionf;
    }

    public Vector3f getEulerRotation(){
        return this.rotation.getEulerAnglesXYZ(new Vector3f());
    }

    public void setEulerRotation(Vector3f vector3f){
        this.rotation.rotationXYZ(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public PartPose asPartPose(){
        Vector3f vector3f = new Vector3f();
        this.rotation.getEulerAnglesXYZ(vector3f);
        return PartPose.offsetAndRotation(
                this.x,
                this.y,
                this.z,
                vector3f.x(),
                vector3f.y(),
                vector3f.z()
        );
    }

    public MutablePartPose translate(Vector3f translation, boolean localSpace){
        if(translation.x() != 0 || translation.y() != 0 || translation.z() != 0){
            if(localSpace){
                translation.rotateX(this.getEulerRotation().x());
                translation.rotateY(this.getEulerRotation().y());
                translation.rotateZ(this.getEulerRotation().z());


                //translation.rotate(this.rotation);
            }
            this.x += translation.x();
            this.y += translation.y();
            this.z += translation.z();
        }
        return this;
    }

    public MutablePartPose rotate(Quaternionf rotation, boolean localSpace){
        if(localSpace){

            /*
            Vector3f eulerRotation = this.getEulerRotation();
            rotation.rotateX(eulerRotation.x());
            rotation.rotateY(eulerRotation.y());
            rotation.rotateZ(eulerRotation.z());

             */




            /*
            Vector3f eulerRotation = rotation.getEulerAnglesXYZ(new Vector3f());


            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            poseStack.setIdentity();
            poseStack.mulPose(this.rotation);

            poseStack.pushPose();
            poseStack.mulPose(rotation);


            this.rotation = poseStack.last().pose().getNormalizedRotation(new Quaternionf());

            poseStack.popPose();
            poseStack.popPose();

             */




            Quaternionf newRotation = new Quaternionf(rotation.x(), rotation.y(), rotation.z(), rotation.w());
            Quaternionf oldRotation = new Quaternionf(this.rotation.x(), this.rotation.y(), this.rotation.z(), this.rotation.w());
            this.rotation = newRotation.mul(oldRotation);




            /*
            Vector3f rotationOriginal = this.getEulerRotation();
            Vector3f rotationAdded = rotation.getEulerAnglesXYZ(new Vector3f());
            rotationOriginal.add(rotationAdded);
            this.setEulerRotation(rotationOriginal);

             */
        } else {
            this.rotation.mul(rotation);

            //Vector3f rotationOriginal = this.rotation.getEulerAnglesXYZ(new Vector3f());
            //Vector3f rotationAdded = rotation.getEulerAnglesZYX(new Vector3f());
            //this.rotation.mul(new Quaternionf().rotationZYX(rotationAdded.z(), rotationAdded.y(), rotationAdded.x()));

        }
        return this;
    }

    public MutablePartPose rotate(Vector3f rotation, boolean localSpace){
        return this.rotate(new Quaternionf().rotationXYZ(rotation.x(), rotation.y(), rotation.z()), localSpace);
    }

    public MutablePartPose add(MutablePartPose partPose){
        this.translate(partPose.getTranslation(), false);
        this.rotate(partPose.rotation, false);
        return this;
    }

    public MutablePartPose subtract(MutablePartPose partPose){
        this.translate(partPose.getTranslation().negate(), false);
        //this.rotate(partPose.rotation.conjugate(), false);
        this.rotation = this.rotation.difference(partPose.rotation);
        return this;
    }

    public MutablePartPose getMirrored(){
        MutablePartPose mutablePartPose = this.getCopy();
        mutablePartPose.x = -mutablePartPose.x;
        Vector3f rotation = mutablePartPose.getEulerRotation();
        mutablePartPose.setEulerRotation(new Vector3f(rotation.x(), -rotation.y(), -rotation.z()));
        return mutablePartPose;
    }

    public MutablePartPose blend(MutablePartPose partPose, float alpha, Easing easing){

        alpha = easing.ease(alpha);
        //Quaternionf quaternionA = new Quaternionf().rotationXYZ(this.xRot, this.yRot, this.zRot);
        //Quaternionf quaternionB = new Quaternionf().rotationXYZ(partPose.xRot, partPose.yRot, partPose.zRot);
        this.rotation.slerp(partPose.rotation, alpha);


        this.x = Mth.lerp(alpha, this.x, partPose.x);
        this.y = Mth.lerp(alpha, this.y, partPose.y);
        this.z = Mth.lerp(alpha, this.z, partPose.z);
        /*
        this.xRot = Mth.lerp(alpha, this.xRot, partPose.xRot);
        this.yRot = Mth.lerp(alpha, this.yRot, partPose.yRot);
        this.zRot = Mth.lerp(alpha, this.zRot, partPose.zRot);
         */
        return this;
    }

    public MutablePartPose blendLinear(MutablePartPose partPose, float alpha){
        return this.blend(partPose, alpha, Easing.Linear.of());
    }

    public static MutablePartPose getMutablePartPoseFromChannelTimeline(ResourceLocation resourceLocation, String partName, float time){
        if(TimelineGroupData.INSTANCE.getHashMap().containsKey(resourceLocation)){
            ChannelTimeline channelTimeline = TimelineGroupData.INSTANCE.get(resourceLocation).getPartTimeline(partName);
            return new MutablePartPose(
                    channelTimeline.getValueAt(TransformChannel.x, time),
                    channelTimeline.getValueAt(TransformChannel.y, time),
                    channelTimeline.getValueAt(TransformChannel.z, time),
                    channelTimeline.getRotationAt(time)
            );
        } else {
            return MutablePartPose.ZERO;
        }
    }

    public static MutablePartPose fromPartPose(PartPose partPose){
        return fromTranslationAndRotation(
                partPose.x,
                partPose.y,
                partPose.z,
                partPose.xRot,
                partPose.yRot,
                partPose.zRot
        );
    }

    public MutablePartPose getCopy(){
        return MutablePartPose.fromPartPose(this.asPartPose());
    }

    public void translatePoseStack(PoseStack poseStack){
        poseStack.translate((this.x / 16.0F), (this.y / 16.0F), (this.z / 16.0F));
    }

    public void rotatePoseStack(PoseStack poseStack){

        Vector3f vector3f = this.getEulerRotation();
        poseStack.mulPose(new Quaternionf().rotationZYX(vector3f.z(), vector3f.y(), vector3f.x()));


        //poseStack.mulPose(this.rotation);


        /*
        if (this.zRot != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(this.zRot));
        }

        if (this.yRot != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(this.yRot));
        }

        if (this.xRot != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(this.xRot));
        }

         */
    }

    public void translateAndRotatePoseStack(PoseStack poseStack){
        translatePoseStack(poseStack);
        rotatePoseStack(poseStack);
    }

    public void translatePoseStackInverse(PoseStack poseStack){
        poseStack.translate((this.x / -16.0F), (this.y / -16.0F), (this.z / -16.0F));
    }

    public void rotatePoseStackInverse(PoseStack poseStack){
        poseStack.mulPose(this.rotation.invert());
        /*
        if (this.xRot != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(-this.xRot));
        }

        if (this.yRot != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(-this.yRot));
        }

        if (this.zRot != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(-this.zRot));
        }

         */
    }

    public void transformPoseStack(PoseStack poseStack, float transformMultiplier){
        poseStack.translate(this.x / transformMultiplier, this.y / transformMultiplier, this.z / transformMultiplier);
        this.rotatePoseStack(poseStack);
        //poseStack.mulPose(this.rotation);
        /*
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            poseStack.mulPose(new Quaternionf().rotationZYX(this.zRot, this.yRot, this.xRot));
        }

         */
    }

    public void transformPoseStack(PoseStack poseStack){
        this.transformPoseStack(poseStack, 16F);
    }

    public void transformModelPart(ModelPart modelPart){
        modelPart.setPos(this.x, this.y, this.z);
        Vector3f vector3f = this.getEulerRotation();
        modelPart.setRotation(vector3f.x(), vector3f.y(), vector3f.z());
    }
}
