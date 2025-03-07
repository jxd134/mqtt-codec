/*
 * Copyright 2020 Rapidw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rapidw.jmh.mqtt.codec;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCounted;

public abstract class AbstractEmbeddedChannelWriteReleaseHandlerContext
    extends AbstractEmbeddedChannelHandlerContext {
    protected AbstractEmbeddedChannelWriteReleaseHandlerContext(
        ByteBufAllocator alloc, ChannelHandler handler) {
        this(alloc, handler, new EmbeddedChannel());
    }

    protected AbstractEmbeddedChannelWriteReleaseHandlerContext(
        ByteBufAllocator alloc, ChannelHandler handler, EmbeddedChannel channel) {
        super(alloc, handler, channel);
    }

    @Override
    protected abstract void handleException(Throwable t);

    @Override
    public final ChannelFuture write(Object msg) {
        return write(msg, newPromise());
    }

    @Override
    public final ChannelFuture write(Object msg, ChannelPromise promise) {
        try {
            if (msg instanceof ReferenceCounted) {
                ((ReferenceCounted) msg).release();
                promise.setSuccess();
            } else {
                channel().write(msg, promise);
            }
        } catch (Exception e) {
            promise.setFailure(e);
            handleException(e);
        }
        return promise;
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        try {
            if (msg instanceof ReferenceCounted) {
                ((ReferenceCounted) msg).release();
                promise.setSuccess();
            } else {
                channel().writeAndFlush(msg, promise);
            }
        } catch (Exception e) {
            promise.setFailure(e);
            handleException(e);
        }
        return promise;
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg) {
        return writeAndFlush(msg, newPromise());
    }
}
